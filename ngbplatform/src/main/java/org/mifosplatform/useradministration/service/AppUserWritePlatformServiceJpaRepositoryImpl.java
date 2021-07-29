/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.exception.ConfigurationPropertyNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.PlatformEmailSendException;
import org.mifosplatform.infrastructure.security.service.PlatformPasswordEncoder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.message.service.MessageGmailBackedPlatformEmailService;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.useradministration.domain.AppUserRepository;
import org.mifosplatform.useradministration.domain.Role;
import org.mifosplatform.useradministration.domain.RoleRepository;
import org.mifosplatform.useradministration.domain.UserDomainService;
import org.mifosplatform.useradministration.exception.RoleNotFoundException;
import org.mifosplatform.useradministration.exception.UserNotFoundException;
import org.mifosplatform.useradministration.serialization.UsersCommandFromApiJsonDeserializerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class AppUserWritePlatformServiceJpaRepositoryImpl implements AppUserWritePlatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(AppUserWritePlatformServiceJpaRepositoryImpl.class);

	private final PlatformSecurityContext context;
	private final UserDomainService userDomainService;
	private final PlatformPasswordEncoder platformPasswordEncoder;
	private final AppUserRepository appUserRepository;
	private final OfficeRepository officeRepository;
	private final RoleRepository roleRepository;
	private final UsersCommandFromApiJsonDeserializerHelper fromApiJsonDeserializer;
	private final MessageGmailBackedPlatformEmailService messageGmailBackedPlatformEmailService;
	private final ConfigurationRepository repository;

	private String authuser;
	private String encodedPassword;
	private String authpwd;
	private String hostName;
	private int portNumber;
	private String port;
	private String starttlsValue;
	private String setContentString;
	private Configuration configuration;

	@Autowired
	public AppUserWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
			final AppUserRepository appUserRepository, final UserDomainService userDomainService,
			final OfficeRepository officeRepository, final RoleRepository roleRepository,
			final PlatformPasswordEncoder platformPasswordEncoder,
			final UsersCommandFromApiJsonDeserializerHelper fromApiJsonDeserializer,
			final MessageGmailBackedPlatformEmailService messageGmailBackedPlatformEmailService,
			final ConfigurationRepository repository) {

		this.context = context;
		this.appUserRepository = appUserRepository;
		this.userDomainService = userDomainService;
		this.officeRepository = officeRepository;
		this.roleRepository = roleRepository;
		this.platformPasswordEncoder = platformPasswordEncoder;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.messageGmailBackedPlatformEmailService = messageGmailBackedPlatformEmailService;
		this.repository = repository;

	}

	@Transactional
	@Override
	@Caching(evict = { @CacheEvict(value = "users", allEntries = true),
			@CacheEvict(value = "usersByUsername", allEntries = true) })
	public CommandProcessingResult createUser(final JsonCommand command) {

		try {
			this.context.authenticatedUser();

			this.fromApiJsonDeserializer.validateForCreate(command.json());

			final String officeIdParamName = "officeId";
			final Long officeId = command.longValueOfParameterNamed(officeIdParamName);

			final Office userOffice = this.officeRepository.findOne(officeId);
			if (userOffice == null) {
				throw new OfficeNotFoundException(officeId);
			}

			final String[] roles = command.arrayValueOfParameterNamed("roles");
			final Set<Role> allRoles = assembleSetOfRoles(roles);

			final AppUser appUser = AppUser.fromJson(userOffice, allRoles, command);
			final Boolean sendPasswordToEmail = command.booleanObjectValueOfParameterNamed("sendPasswordToEmail");
			this.userDomainService.create(appUser, sendPasswordToEmail);

			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(appUser.getId()) //
					.withOfficeId(userOffice.getId()) //
					.build();
		} catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		} catch (final PlatformEmailSendException e) {
			final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

			final String email = command.stringValueOfParameterNamed("email");
			final ApiParameterError error = ApiParameterError.parameterError("error.msg.user.email.invalid",
					"The parameter email is invalid.", "email", email);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}

	@Transactional
	@Override
	@Caching(evict = { @CacheEvict(value = "users", allEntries = true),
			@CacheEvict(value = "usersByUsername", allEntries = true) })
	public CommandProcessingResult updateUser(final Long userId, final JsonCommand command) {

		try {
			this.context.authenticatedUser();

			this.fromApiJsonDeserializer.validateForUpdate(command.json());

			final AppUser userToUpdate = this.appUserRepository.findOne(userId);
			if (userToUpdate == null) {
				throw new UserNotFoundException(userId);
			}

			final Map<String, Object> changes = userToUpdate.update(command, this.platformPasswordEncoder);

			if (changes.containsKey("officeId")) {
				final Long officeId = (Long) changes.get("officeId");
				final Office office = this.officeRepository.findOne(officeId);
				if (office == null) {
					throw new OfficeNotFoundException(officeId);
				}

				userToUpdate.changeOffice(office);
			}

			if (changes.containsKey("roles")) {

				final String[] roleIds = (String[]) changes.get("roles");
				final Set<Role> allRoles = assembleSetOfRoles(roleIds);

				userToUpdate.updateRoles(allRoles);
			}

			if (!changes.isEmpty()) {
				this.appUserRepository.saveAndFlush(userToUpdate);
			}

			return new CommandProcessingResultBuilder() //
					.withEntityId(userId) //
					.withOfficeId(userToUpdate.getOffice().getId()) //
					.with(changes) //
					.build();
		} catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	private Set<Role> assembleSetOfRoles(final String[] rolesArray) {

		final Set<Role> allRoles = new HashSet<Role>();

		if (!ObjectUtils.isEmpty(rolesArray)) {
			for (final String roleId : rolesArray) {
				final Long id = Long.valueOf(roleId);
				final Role role = this.roleRepository.findOne(id);
				if (role == null) {
					throw new RoleNotFoundException(id);
				}
				allRoles.add(role);
			}
		}

		return allRoles;
	}

	@Transactional
	@Override
	@Caching(evict = { @CacheEvict(value = "users", allEntries = true),
			@CacheEvict(value = "usersByUsername", allEntries = true) })
	public CommandProcessingResult deleteUser(final Long userId) {

		final AppUser user = this.appUserRepository.findOne(userId);
		if (user == null || user.isDeleted()) {
			throw new UserNotFoundException(userId);
		}

		user.delete();
		this.appUserRepository.save(user);

		return new CommandProcessingResultBuilder().withEntityId(userId).withOfficeId(user.getOffice().getId()).build();
	}

	/*
	 * Guaranteed to throw an exception no matter what the data integrity issue is.
	 */
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("username_org")) {
			final String username = command.stringValueOfParameterNamed("username");
			final StringBuilder defaultMessageBuilder = new StringBuilder("User with username ").append(username)
					.append(" already exists.");
			throw new PlatformDataIntegrityException("error.msg.user.duplicate.username",
					defaultMessageBuilder.toString(), "username", username);
		}

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource.");
	}

	@Override
	public CommandProcessingResult generateKey(Long userId) {
		// TODO Auto-generated method stub
		String secretKey = null;
		AppUser user = this.appUserRepository.findOne(userId);
		if (Optional.ofNullable(user).isPresent()) {
			String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
			// create StringBuffer size of AlphaNumericString
			StringBuilder sb = new StringBuilder(20);
			for (int i = 0; i < 20; i++) {
				// generate a random number between
				// 0 to AlphaNumericString variable length
				int index = (int) (AlphaNumericString.length() * Math.random());

				// add Character one by one in end of sb
				sb.append(AlphaNumericString.charAt(index));
			}
			secretKey = sb.toString();
			configuration = repository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_SMTP);

			String value = configuration.getValue();
			try {
				JSONObject object = new JSONObject(value);

				authuser = (String) object.get("mailId");

				encodedPassword = (String) object.get("password");
				authpwd = new String(Base64.decodeBase64(encodedPassword));
				hostName = (String) object.get("hostName");
				port = object.getString("port");
				if (port.isEmpty()) {
					portNumber = Integer.parseInt("25");
				} else {
					portNumber = Integer.parseInt(port);
				}
				starttlsValue = (String) object.get("starttls");
				setContentString = (String) object.get("setContentString");
			} catch (Exception e) {
				throw new PlatformDataIntegrityException("Json exception", "Json Exception", "Json Exception");
			}

			if (configuration != null) {
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", starttlsValue);
				props.put("mail.smtp.host", hostName);
				props.put("mail.smtp.port", portNumber);
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

				Session session = Session.getInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(authuser, authpwd);
					}
				});

				try {

					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(authuser));
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
					message.setSubject("Password reset link for moreplex ");
					String BasicBase64format = Base64.encodeBase64String(secretKey.getBytes());
					message.setText("Dear user please click below link to reset your password" + "\n"
							+ "https://billing.moreplex.com/#/change-password/" + "" + userId + "" + "/"
							+ BasicBase64format);
					System.out.println("Sending");
					Transport.send(message);
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.WEEK_OF_YEAR, 2);
					user.setSecretKey(secretKey);
					user.setSecretKeyExpiryTime(calendar.getTime());
					user.setSecretKeyStatus(false);
					appUserRepository.saveAndFlush(user);
					System.out.println("Done");

				} catch (MessagingException e) {
					e.printStackTrace();
					throw new PlatformDataIntegrityException("Wrong Authuser or Password",
							"error.authentication failure", "Please confirm from address email and password");

				}

			} else {
				throw new ConfigurationPropertyNotFoundException("SMTP GlobalConfiguration Property Not Found");

			}
		}
		return new CommandProcessingResultBuilder().withResourceIdAsString(user.getEmail()).build();

	}

	@Transactional
	@Override
	public CommandProcessingResult validateKey(final Long userId, JsonCommand command) {
		// TODO Auto-generated method stub

		final String key = new String(Base64.decodeBase64(command.stringValueOfParameterName("secretKey")));
		AppUser user = this.appUserRepository.findBySecretKey(userId, key);
		if (Optional.ofNullable(user).isPresent()) {
			if (key.equals(user.getSecretKey())) {
				System.out.println("Success");
				user.setSecretKeyStatus(true);
				this.appUserRepository.save(user);
			} else {
				throw new PlatformDataIntegrityException("error.secrety key. invalid",
						"try to generate new one to reset yor passcode", key, "invalid.secret key or expired");
			}
		} else {
			throw new PlatformDataIntegrityException("error.secrety key. invalid",
					"try to generate new one to reset yor passcode", key, "invalid.secret key or expired");
		}

		return new CommandProcessingResultBuilder().withEntityId(userId).build();

	}

}