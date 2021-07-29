package org.mifosplatform.provisioning.processscheduledjobs.service;

public interface SheduleJobWritePlatformService {


	void processInvoice();
	
	void processRequest();
	
	void processSimulator();
	
	void generateStatment();
	
	void processingMessages();
	
	void processingAutoExipryOrders();
	
	//void processNotify();
	
	void processMiddleware();
	
	void eventActionProcessor();
	
	void reportEmail();
	
	void reportStatmentPdf();
    
	void processExportData();

	void processPartnersCommission();

	void reProcessEventAction();

	void processAgingDistribution();

	void processingDisconnectUnpaidCustomers();

	void process(Long id);

	//void processNotify(Long id);

	void processNotify();
	
	void processingEscalations();

	void processCatalogue();

	void inActiveCustomersWithSTBLease();

}
