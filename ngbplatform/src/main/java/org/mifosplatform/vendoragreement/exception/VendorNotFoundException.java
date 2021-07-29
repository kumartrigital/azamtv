package org.mifosplatform.vendoragreement.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class VendorNotFoundException extends AbstractPlatformResourceNotFoundException {

public VendorNotFoundException(String string) {
super("error.msg.vendor.id.not.found","Vendor is Not Found");
}

}
