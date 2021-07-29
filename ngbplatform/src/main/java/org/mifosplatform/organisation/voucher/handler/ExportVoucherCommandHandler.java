package org.mifosplatform.organisation.voucher.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.voucher.service.VoucherWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "EXPORT_VOUCHER", action = "INSERT")
public class ExportVoucherCommandHandler implements NewCommandSourceHandler{

	private final VoucherWritePlatformService voucherWritePlatformService;

	@Autowired
	public ExportVoucherCommandHandler(final VoucherWritePlatformService voucherWritePlatformService) {
		this.voucherWritePlatformService = voucherWritePlatformService;
	}
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		
		return voucherWritePlatformService.exportVoucher(command,command.entityId());
	}

}
