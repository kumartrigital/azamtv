package org.mifosplatform.organisation.channel.domain;

public enum LanguageEnum {
	
	Telugu(0,"telugu"),
	English(1,"English"),
	Bengali(2,"Bengali"),
	Assameese(3,"Assameese"),
	Urdu(4,"Urdu"),
	Hindi(5,"Hindi"),
	Odia(6,"Odia"),
	Gujrati(7,"Gujrati"),
	Marathi(8,"Marathi"),
	Punjabi(9,"Punjabi"),
	Tamil(10,"Tamil"),
	Malayalam(11,"Malayalam"),
	Bhojpuri(12,"Bhojpuri"),
	Kannada(13,"Kannada"),
	German(14,"German"),
	Rajasthani(15,"Rajasthani");
	
	private final Integer id;
	private final String Name;

    private LanguageEnum(final Integer id, final String Name) {
        this.id = id;
		this.Name = Name;
    }

    public Integer getValue() {
        return this.id;
    }

	public String getCode() {
		return Name;
	}
	public static LanguageEnum fromInt(final Integer frequency) {

		LanguageEnum languageEnum = LanguageEnum.Telugu;
		switch (frequency) {
		case 1:
			languageEnum = LanguageEnum.English;
			break;
		case 2:
			languageEnum = LanguageEnum.Bengali;
			break;
		case 3:
			languageEnum = LanguageEnum.Assameese;
			break;
		
		case 4:
			languageEnum = LanguageEnum.Urdu;
			break;
		case 5:
			languageEnum = LanguageEnum.Hindi;
			break;
		case 6:
			languageEnum = LanguageEnum.Odia;
			break;
		case 7:
			languageEnum = LanguageEnum.Gujrati;
			break;
		case 8:
			languageEnum = LanguageEnum.Marathi;
			break;
		case 9:
			languageEnum = LanguageEnum.Punjabi;
			break;
		case 10:
			languageEnum = LanguageEnum.Tamil;
			break;
		case 11:
			languageEnum = LanguageEnum.Malayalam;
			break;
		case 12:
			languageEnum = LanguageEnum.Bhojpuri;
			break;
		case 13:
			languageEnum = LanguageEnum.Kannada;
			break;
		case 14:
			languageEnum = LanguageEnum.German;
			break;
		case 15:
			languageEnum = LanguageEnum.Rajasthani;
			break;
		default:
			languageEnum = LanguageEnum.Telugu;
			break;
		}
		return languageEnum;
	
}

}