
package org.mifosplatform.obrm.service;


import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Stub;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.databinding.ADBException;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutInAxisOperation;



public class InfranetWebServiceServiceStub extends Stub{

    protected AxisOperation[] _operations;
    private final static String url = "http://192.168.1.5:8080/infranetwebsvc/services/BRMBaseServices";

    //hashmaps to keep the fault mapping
    private HashMap faultExceptionNameMap = new HashMap();
    private HashMap faultExceptionClassNameMap = new HashMap();
    private HashMap faultMessageMap = new HashMap();

    private static int counter = 0;

    private static synchronized java.lang.String getUniqueSuffix(){
        // reset the counter if it is greater than 99999
        if (counter > 99999){
            counter = 0;
        }
        counter = counter + 1; 
        return java.lang.Long.toString(java.lang.System.currentTimeMillis()) + "_" + counter;
    }


private void populateAxisService() throws AxisFault {

 //creating the Service with a unique name
 _service = new AxisService("InfranetWebServiceService" + getUniqueSuffix());
 addAnonymousOperations();

    //creating the operations
    org.apache.axis2.description.AxisOperation __operation;

    _operations = new org.apache.axis2.description.AxisOperation[3];
    
               __operation = new OutInAxisOperation();
            

        __operation.setName(new QName(url, "opcodeWithFlags"));
    _service.addOperation(__operation);
    

    
    
        _operations[0]=__operation;
        
    
               __operation = new OutInAxisOperation();
            

        __operation.setName(new QName(url, "test"));
    _service.addOperation(__operation);
    

    
    
        _operations[1]=__operation;
        
    
               __operation = new OutInAxisOperation();
            

        __operation.setName(new QName(url, "opcode"));
    _service.addOperation(__operation);
    

    
    
        _operations[2]=__operation;
        
    
    }

//populates the faults
private void populateFaults(){
     


}

/**
  *Constructor that takes in a configContext
  */

public InfranetWebServiceServiceStub(ConfigurationContext configurationContext,
   java.lang.String targetEndpoint)
   throws AxisFault {
     this(configurationContext,targetEndpoint,false);
}


/**
 * Constructor that takes in a configContext  and useseperate listner
 */
public InfranetWebServiceServiceStub(ConfigurationContext configurationContext,
    java.lang.String targetEndpoint, boolean useSeparateListener)
    throws AxisFault {
     //To populate AxisService
     populateAxisService();
     populateFaults();

    _serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext,_service);
    

    _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(
            targetEndpoint));
    _serviceClient.getOptions().setUseSeparateListener(useSeparateListener);
    

}

/**
 * Default Constructor
 */
public InfranetWebServiceServiceStub(ConfigurationContext configurationContext) throws AxisFault {
    
                this(configurationContext,url );
            
}

/**
 * Default Constructor
 */
public InfranetWebServiceServiceStub() throws AxisFault {
    
                this(url);
            
}

/**
 * Constructor taking the target endpoint
 */
public InfranetWebServiceServiceStub(java.lang.String targetEndpoint) throws AxisFault {
    this(null,targetEndpoint);
}



    
                /**
                 * Auto generated method signature
                 * 
                 * @see InfranetWebServiceService#opcodeWithFlags
                 * @param opcodeWithFlags0
                
                 */

                

                        public  InfranetWebServiceServiceStub.OpcodeWithFlagsResponse opcodeWithFlags(

                       InfranetWebServiceServiceStub.OpcodeWithFlags opcodeWithFlags0)
                    

                throws java.rmi.RemoteException
                
                {
          org.apache.axis2.context.MessageContext _messageContext = null;
          try{
           org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
          _operationClient.getOptions().setAction(url+"/InfranetWebService/opcodeWithFlagsRequest");
          _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

          
          
              addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
          

          // create a message context
          _messageContext = new org.apache.axis2.context.MessageContext();

          

          // create SOAP envelope with that payload
          org.apache.axiom.soap.SOAPEnvelope env = null;
                
                                                
                                                env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                opcodeWithFlags0,
                                                optimizeContent(new javax.xml.namespace.QName(url,
                                                "opcodeWithFlags")), new javax.xml.namespace.QName(url,
                                                "opcodeWithFlags"));
                                            
    //adding SOAP soap_headers
     _serviceClient.addHeadersToEnvelope(env);
    // set the message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message contxt to the operation client
    _operationClient.addMessageContext(_messageContext);

    //execute the operation client
    _operationClient.execute(true);

     
           org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                       org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
            
            
                            java.lang.Object object = fromOM(
                                         _returnEnv.getBody().getFirstElement() ,
                                         InfranetWebServiceServiceStub.OpcodeWithFlagsResponse.class,
                                          getEnvelopeNamespaces(_returnEnv));

                           
                                    return (InfranetWebServiceServiceStub.OpcodeWithFlagsResponse)object;
                               
     }catch(org.apache.axis2.AxisFault f){

        org.apache.axiom.om.OMElement faultElt = f.getDetail();
        if (faultElt!=null){
            if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcodeWithFlags"))){
                //make the fault by reflection
                try{
                    java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcodeWithFlags"));
                    java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                    java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(java.lang.String.class);
                    java.lang.Exception ex = (java.lang.Exception) constructor.newInstance(f.getMessage());
                    //message class
                    java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcodeWithFlags"));
                    java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                    java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                    java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                               new java.lang.Class[]{messageClass});
                    m.invoke(ex,new java.lang.Object[]{messageObject});
                    

                    throw new java.rmi.RemoteException(ex.getMessage(), ex);
                }catch(java.lang.ClassCastException e){
                   // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                } catch (java.lang.ClassNotFoundException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }catch (java.lang.NoSuchMethodException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                } catch (java.lang.reflect.InvocationTargetException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }  catch (java.lang.IllegalAccessException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }   catch (java.lang.InstantiationException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }
            }else{
                throw f;
            }
        }else{
            throw f;
        }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                  _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }
        
            /**
            * Auto generated method signature for Asynchronous Invocations
            * 
            * @see InfranetWebServiceService#startopcodeWithFlags
                * @param opcodeWithFlags0
            
            */
            public  void startopcodeWithFlags(

             InfranetWebServiceServiceStub.OpcodeWithFlags opcodeWithFlags0,

              final InfranetWebServiceServiceCallbackHandler callback)

            throws java.rmi.RemoteException{

          org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
         _operationClient.getOptions().setAction(url+"/InfranetWebService/opcodeWithFlagsRequest");
         _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

          
          
              addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
          


          // create SOAP envelope with that payload
          org.apache.axiom.soap.SOAPEnvelope env=null;
          final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

                
                                //Style is Doc.
                                
                                                
                                                env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                opcodeWithFlags0,
                                                optimizeContent(new javax.xml.namespace.QName(url,
                                                "opcodeWithFlags")), new javax.xml.namespace.QName(url,
                                                "opcodeWithFlags"));
                                            
    // adding SOAP soap_headers
     _serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


                
                    _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
                        public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
                        try {
                            org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();
                            
                                    java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
                                                                     InfranetWebServiceServiceStub.OpcodeWithFlagsResponse.class,
                                                                     getEnvelopeNamespaces(resultEnv));
                                    callback.receiveResultopcodeWithFlags(
                                    (InfranetWebServiceServiceStub.OpcodeWithFlagsResponse)object);
                                    
                        } catch (org.apache.axis2.AxisFault e) {
                            callback.receiveErroropcodeWithFlags(e);
                        }
                        }

                        public void onError(java.lang.Exception error) {
							if (error instanceof org.apache.axis2.AxisFault) {
								org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
								org.apache.axiom.om.OMElement faultElt = f.getDetail();
								if (faultElt!=null){
									if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcodeWithFlags"))){
										//make the fault by reflection
										try{
												java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcodeWithFlags"));
												java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
												java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(java.lang.String.class);
                                                java.lang.Exception ex = (java.lang.Exception) constructor.newInstance(f.getMessage());
												//message class
												java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcodeWithFlags"));
													java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
												java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
												java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
														new java.lang.Class[]{messageClass});
												m.invoke(ex,new java.lang.Object[]{messageObject});
												
				
									            callback.receiveErroropcodeWithFlags(new java.rmi.RemoteException(ex.getMessage(), ex));
                                        } catch(java.lang.ClassCastException e){
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcodeWithFlags(f);
                                        } catch (java.lang.ClassNotFoundException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcodeWithFlags(f);
                                        } catch (java.lang.NoSuchMethodException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcodeWithFlags(f);
                                        } catch (java.lang.reflect.InvocationTargetException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcodeWithFlags(f);
                                        } catch (java.lang.IllegalAccessException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcodeWithFlags(f);
                                        } catch (java.lang.InstantiationException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcodeWithFlags(f);
                                        } catch (org.apache.axis2.AxisFault e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcodeWithFlags(f);
                                        }
								    } else {
									    callback.receiveErroropcodeWithFlags(f);
								    }
								} else {
								    callback.receiveErroropcodeWithFlags(f);
								}
							} else {
							    callback.receiveErroropcodeWithFlags(error);
							}
                        }

                        public void onFault(org.apache.axis2.context.MessageContext faultContext) {
                            org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
                            onError(fault);
                        }

                        public void onComplete() {
                            try {
                                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                            } catch (org.apache.axis2.AxisFault axisFault) {
                                callback.receiveErroropcodeWithFlags(axisFault);
                            }
                        }
            });
                    

      org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if ( _operations[0].getMessageReceiver()==null &&  _operationClient.getOptions().isUseSeparateListener()) {
       _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      _operations[0].setMessageReceiver(
                _callbackReceiver);
    }

       //execute the operation client
       _operationClient.execute(false);

                }
            
                /**
                 * Auto generated method signature
                 * 
                 * @see InfranetWebServiceService#test
                 * @param test2
                
                 */

                

                        public  InfranetWebServiceServiceStub.TestResponse test(

                        InfranetWebServiceServiceStub.Test test2)
                    

                throws java.rmi.RemoteException
                
                {
          org.apache.axis2.context.MessageContext _messageContext = null;
          try{
           org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
          _operationClient.getOptions().setAction(url+"/InfranetWebService/testRequest");
          _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

          
          
              addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
          

          // create a message context
          _messageContext = new org.apache.axis2.context.MessageContext();

          

          // create SOAP envelope with that payload
          org.apache.axiom.soap.SOAPEnvelope env = null;
                
                                                
                                                env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                test2,
                                                optimizeContent(new javax.xml.namespace.QName(url,
                                                "test")), new javax.xml.namespace.QName(url,
                                                "test"));
                                            
    //adding SOAP soap_headers
     _serviceClient.addHeadersToEnvelope(env);
    // set the message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message contxt to the operation client
    _operationClient.addMessageContext(_messageContext);

    //execute the operation client
    _operationClient.execute(true);

     
           org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                       org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
            
            
                            java.lang.Object object = fromOM(
                                         _returnEnv.getBody().getFirstElement() ,
                                         InfranetWebServiceServiceStub.TestResponse.class,
                                          getEnvelopeNamespaces(_returnEnv));

                           
                                    return (InfranetWebServiceServiceStub.TestResponse)object;
                               
     }catch(org.apache.axis2.AxisFault f){

        org.apache.axiom.om.OMElement faultElt = f.getDetail();
        if (faultElt!=null){
            if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"test"))){
                //make the fault by reflection
                try{
                    java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"test"));
                    java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                    java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(java.lang.String.class);
                    java.lang.Exception ex = (java.lang.Exception) constructor.newInstance(f.getMessage());
                    //message class
                    java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"test"));
                    java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                    java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                    java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                               new java.lang.Class[]{messageClass});
                    m.invoke(ex,new java.lang.Object[]{messageObject});
                    

                    throw new java.rmi.RemoteException(ex.getMessage(), ex);
                }catch(java.lang.ClassCastException e){
                   // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                } catch (java.lang.ClassNotFoundException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }catch (java.lang.NoSuchMethodException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                } catch (java.lang.reflect.InvocationTargetException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }  catch (java.lang.IllegalAccessException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }   catch (java.lang.InstantiationException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }
            }else{
                throw f;
            }
        }else{
            throw f;
        }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                  _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }
        
            /**
            * Auto generated method signature for Asynchronous Invocations
            * 
            * @see InfranetWebServiceService#starttest
                * @param test2
            
            */
            public  void starttest(

             InfranetWebServiceServiceStub.Test test2,

              final InfranetWebServiceServiceCallbackHandler callback)

            throws java.rmi.RemoteException{

          org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
         _operationClient.getOptions().setAction(url+"/InfranetWebService/testRequest");
         _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

          
          
              addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
          


          // create SOAP envelope with that payload
          org.apache.axiom.soap.SOAPEnvelope env=null;
          final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

                
                                //Style is Doc.
                                
                                                
                                                env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                test2,
                                                optimizeContent(new javax.xml.namespace.QName(url,
                                                "test")), new javax.xml.namespace.QName(url,
                                                "test"));
                                            
    // adding SOAP soap_headers
     _serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


                
                    _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
                        public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
                        try {
                            org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();
                            
                                    java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
                                                                     InfranetWebServiceServiceStub.TestResponse.class,
                                                                     getEnvelopeNamespaces(resultEnv));
                                    callback.receiveResulttest(
                                    (InfranetWebServiceServiceStub.TestResponse)object);
                                    
                        } catch (org.apache.axis2.AxisFault e) {
                            callback.receiveErrortest(e);
                        }
                        }

                        public void onError(java.lang.Exception error) {
							if (error instanceof org.apache.axis2.AxisFault) {
								org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
								org.apache.axiom.om.OMElement faultElt = f.getDetail();
								if (faultElt!=null){
									if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"test"))){
										//make the fault by reflection
										try{
												java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"test"));
												java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
												java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(java.lang.String.class);
                                                java.lang.Exception ex = (java.lang.Exception) constructor.newInstance(f.getMessage());
												//message class
												java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"test"));
													java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
												java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
												java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
														new java.lang.Class[]{messageClass});
												m.invoke(ex,new java.lang.Object[]{messageObject});
												
				
									            callback.receiveErrortest(new java.rmi.RemoteException(ex.getMessage(), ex));
                                        } catch(java.lang.ClassCastException e){
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErrortest(f);
                                        } catch (java.lang.ClassNotFoundException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErrortest(f);
                                        } catch (java.lang.NoSuchMethodException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErrortest(f);
                                        } catch (java.lang.reflect.InvocationTargetException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErrortest(f);
                                        } catch (java.lang.IllegalAccessException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErrortest(f);
                                        } catch (java.lang.InstantiationException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErrortest(f);
                                        } catch (org.apache.axis2.AxisFault e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErrortest(f);
                                        }
								    } else {
									    callback.receiveErrortest(f);
								    }
								} else {
								    callback.receiveErrortest(f);
								}
							} else {
							    callback.receiveErrortest(error);
							}
                        }

                        public void onFault(org.apache.axis2.context.MessageContext faultContext) {
                            org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
                            onError(fault);
                        }

                        public void onComplete() {
                            try {
                                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                            } catch (org.apache.axis2.AxisFault axisFault) {
                                callback.receiveErrortest(axisFault);
                            }
                        }
            });
                    

      org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if ( _operations[1].getMessageReceiver()==null &&  _operationClient.getOptions().isUseSeparateListener()) {
       _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      _operations[1].setMessageReceiver(
                _callbackReceiver);
    }

       //execute the operation client
       _operationClient.execute(false);

                }
            
                /**
                 * Auto generated method signature
                 * 
                 * @see InfranetWebServiceService#opcode
                 * @param opcode4
                
                 */

                

                        public  InfranetWebServiceServiceStub.OpcodeResponse opcode(

                        InfranetWebServiceServiceStub.Opcode opcode4)
                    

                throws java.rmi.RemoteException
                
                {
          org.apache.axis2.context.MessageContext _messageContext = null;
          try{
           org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[2].getName());
          _operationClient.getOptions().setAction(url+"/InfranetWebService/opcodeRequest");
          _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

          
          
              addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
          

          // create a message context
          _messageContext = new org.apache.axis2.context.MessageContext();

          

          // create SOAP envelope with that payload
          org.apache.axiom.soap.SOAPEnvelope env = null;
                
                                                
                                                env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                opcode4,
                                                optimizeContent(new javax.xml.namespace.QName(url,
                                                "opcode")), new javax.xml.namespace.QName(url,
                                                "opcode"));
                                            
    //adding SOAP soap_headers
     _serviceClient.addHeadersToEnvelope(env);
    // set the message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message contxt to the operation client
    _operationClient.addMessageContext(_messageContext);

    //execute the operation client
    _operationClient.execute(true);

     
           org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                       org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
            
            
                            java.lang.Object object = fromOM(
                                         _returnEnv.getBody().getFirstElement() ,
                                         InfranetWebServiceServiceStub.OpcodeResponse.class,
                                          getEnvelopeNamespaces(_returnEnv));

                           
                                    return (InfranetWebServiceServiceStub.OpcodeResponse)object;
                               
     }catch(org.apache.axis2.AxisFault f){

        org.apache.axiom.om.OMElement faultElt = f.getDetail();
        if (faultElt!=null){
            if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcode"))){
                //make the fault by reflection
                try{
                    java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcode"));
                    java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                    java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(java.lang.String.class);
                    java.lang.Exception ex = (java.lang.Exception) constructor.newInstance(f.getMessage());
                    //message class
                    java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcode"));
                    java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                    java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                    java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                               new java.lang.Class[]{messageClass});
                    m.invoke(ex,new java.lang.Object[]{messageObject});
                    

                    throw new java.rmi.RemoteException(ex.getMessage(), ex);
                }catch(java.lang.ClassCastException e){
                   // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                } catch (java.lang.ClassNotFoundException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }catch (java.lang.NoSuchMethodException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                } catch (java.lang.reflect.InvocationTargetException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }  catch (java.lang.IllegalAccessException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }   catch (java.lang.InstantiationException e) {
                    // we cannot intantiate the class - throw the original Axis fault
                    throw f;
                }
            }else{
                throw f;
            }
        }else{
            throw f;
        }
        } finally {
            if (_messageContext.getTransportOut() != null) {
                  _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
    }
        
            /**
            * Auto generated method signature for Asynchronous Invocations
            * 
            * @see InfranetWebServiceService#startopcode
                * @param opcode4
            
            */
            public  void startopcode(

             InfranetWebServiceServiceStub.Opcode opcode4,

              final InfranetWebServiceServiceCallbackHandler callback)

            throws java.rmi.RemoteException{

          org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[2].getName());
         _operationClient.getOptions().setAction(url+"/InfranetWebService/opcodeRequest");
         _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

          
          
              addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
          


          // create SOAP envelope with that payload
          org.apache.axiom.soap.SOAPEnvelope env=null;
          final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

                
                                //Style is Doc.
                                
                                                
                                                env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                opcode4,
                                                optimizeContent(new javax.xml.namespace.QName(url,
                                                "opcode")), new javax.xml.namespace.QName(url,
                                                "opcode"));
                                            
    // adding SOAP soap_headers
     _serviceClient.addHeadersToEnvelope(env);
    // create message context with that soap envelope
    _messageContext.setEnvelope(env);

    // add the message context to the operation client
    _operationClient.addMessageContext(_messageContext);


                
                    _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
                        public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
                        try {
                            org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();
                            
                                    java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
                                                                     InfranetWebServiceServiceStub.OpcodeResponse.class,
                                                                     getEnvelopeNamespaces(resultEnv));
                                    callback.receiveResultopcode(
                                    (InfranetWebServiceServiceStub.OpcodeResponse)object);
                                    
                        } catch (org.apache.axis2.AxisFault e) {
                            callback.receiveErroropcode(e);
                        }
                        }

                        public void onError(java.lang.Exception error) {
							if (error instanceof org.apache.axis2.AxisFault) {
								org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
								org.apache.axiom.om.OMElement faultElt = f.getDetail();
								if (faultElt!=null){
									if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcode"))){
										//make the fault by reflection
										try{
												java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcode"));
												java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
												java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(java.lang.String.class);
                                                java.lang.Exception ex = (java.lang.Exception) constructor.newInstance(f.getMessage());
												//message class
												java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),"opcode"));
													java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
												java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
												java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
														new java.lang.Class[]{messageClass});
												m.invoke(ex,new java.lang.Object[]{messageObject});
												
				
									            callback.receiveErroropcode(new java.rmi.RemoteException(ex.getMessage(), ex));
                                        } catch(java.lang.ClassCastException e){
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcode(f);
                                        } catch (java.lang.ClassNotFoundException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcode(f);
                                        } catch (java.lang.NoSuchMethodException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcode(f);
                                        } catch (java.lang.reflect.InvocationTargetException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcode(f);
                                        } catch (java.lang.IllegalAccessException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcode(f);
                                        } catch (java.lang.InstantiationException e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcode(f);
                                        } catch (org.apache.axis2.AxisFault e) {
                                            // we cannot intantiate the class - throw the original Axis fault
                                            callback.receiveErroropcode(f);
                                        }
								    } else {
									    callback.receiveErroropcode(f);
								    }
								} else {
								    callback.receiveErroropcode(f);
								}
							} else {
							    callback.receiveErroropcode(error);
							}
                        }

                        public void onFault(org.apache.axis2.context.MessageContext faultContext) {
                            org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
                            onError(fault);
                        }

                        public void onComplete() {
                            try {
                                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                            } catch (org.apache.axis2.AxisFault axisFault) {
                                callback.receiveErroropcode(axisFault);
                            }
                        }
            });
                    

      org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
    if ( _operations[2].getMessageReceiver()==null &&  _operationClient.getOptions().isUseSeparateListener()) {
       _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
      _operations[2].setMessageReceiver(
                _callbackReceiver);
    }

       //execute the operation client
       _operationClient.execute(false);

                }
            


   /**
    *  A utility method that copies the namepaces from the SOAPEnvelope
    */
   private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
    java.util.Map returnMap = new java.util.HashMap();
    java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
    while (namespaceIterator.hasNext()) {
        org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
        returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
    }
   return returnMap;
}



private javax.xml.namespace.QName[] opNameArray = null;
private boolean optimizeContent(javax.xml.namespace.QName opName) {
    

    if (opNameArray == null) {
        return false;
    }
    for (int i = 0; i < opNameArray.length; i++) {
        if (opName.equals(opNameArray[i])) {
            return true;   
        }
    }
    return false;
}
 //http://172.21.0.36:9004/infranetwebsvc/services/BRMBaseServices
    public static class ExtensionMapper{

      public static java.lang.Object getTypeObject(java.lang.String namespaceURI,
                                                   java.lang.String typeName,
                                                   javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{

          
         throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
      }

    }

    public static class Test
    implements org.apache.axis2.databinding.ADBBean{
    
            public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            url,
            "test",
            "ns1");

        

 
 
    /**
    *
    * @param parentQName
    * @param factory
    * @return org.apache.axiom.om.OMElement
    */
   public org.apache.axiom.om.OMElement getOMElement (
           final javax.xml.namespace.QName parentQName,
           final org.apache.axiom.om.OMFactory factory) throws ADBException{


    
           org.apache.axiom.om.OMDataSource dataSource =
                   new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME);
           return factory.createOMElement(dataSource,MY_QNAME);
        
    }

     public void serialize(final javax.xml.namespace.QName parentQName,
                                   javax.xml.stream.XMLStreamWriter xmlWriter)
                            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                       serialize(parentQName,xmlWriter,false);
     }

     public void serialize(final javax.xml.namespace.QName parentQName,
                           javax.xml.stream.XMLStreamWriter xmlWriter,
                           boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        
            


            java.lang.String prefix = null;
            java.lang.String namespace = null;
            

                prefix = parentQName.getPrefix();
                namespace = parentQName.getNamespaceURI();
                writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
            
              if (serializeType){
           

               java.lang.String namespacePrefix = registerPrefix(xmlWriter,url);
               if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       namespacePrefix+":test",
                       xmlWriter);
               } else {
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       "test",
                       xmlWriter);
               }

           
               }
           
                xmlWriter.writeEndElement();
           

    }

    private static java.lang.String generatePrefix(java.lang.String namespace) {
        if(namespace.equals(url)){
            return "ns1";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                   javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
        if (writerPrefix != null) {
            xmlWriter.writeStartElement(namespace, localPart);
        } else {
            if (namespace.length() == 0) {
                prefix = "";
            } else if (prefix == null) {
                prefix = generatePrefix(namespace);
            }

            xmlWriter.writeStartElement(prefix, localPart, namespace);
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
    }
    
    /**
     * Util method to write an attribute with the ns prefix
     */
    private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        xmlWriter.writeAttribute(namespace,attName,attValue);
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName,attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace,attName,attValue);
        }
    }


       /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                         javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }
    /**
     *  method to handle Qnames
     */

    private void writeQName(javax.xml.namespace.QName qname,
                            javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix,namespaceURI);
            }

            if (prefix.trim().length() > 0){
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(javax.xml.namespace.QName[] qnames,
                             javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
            java.lang.String namespaceURI = null;
            java.lang.String prefix = null;

            for (int i = 0; i < qnames.length; i++) {
                if (i > 0) {
                    stringToWrite.append(" ");
                }
                namespaceURI = qnames[i].getNamespaceURI();
                if (namespaceURI != null) {
                    prefix = xmlWriter.getPrefix(namespaceURI);
                    if ((prefix == null) || (prefix.length() == 0)) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix,namespaceURI);
                    }

                    if (prefix.trim().length() > 0){
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                } else {
                    stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                }
            }
            xmlWriter.writeCharacters(stringToWrite.toString());
        }

    }


    /**
     * Register a namespace prefix
     */
    private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);
        if (prefix == null) {
            prefix = generatePrefix(namespace);
            javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
            while (true) {
                java.lang.String uri = nsContext.getNamespaceURI(prefix);
                if (uri == null || uri.length() == 0) {
                    break;
                }
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
            }
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        return prefix;
    }



    /**
    * databinding method to get an XML representation of this object
    *
    */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException{


    
             java.util.ArrayList elementList = new java.util.ArrayList();
             java.util.ArrayList attribList = new java.util.ArrayList();

            

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
        
        

    }



 /**
  *  Factory class that keeps the parse method
  */
public static class Factory{

    
    

    /**
    * static method to create the object
    * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
    *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
    * Postcondition: If this object is an element, the reader is positioned at its end element
    *                If this object is a complex type, the reader is positioned at the end element of its outer element
    */
    public static Test parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
        Test object =
            new Test();

        int event;
        java.lang.String nillableValue = null;
        java.lang.String prefix ="";
        java.lang.String namespaceuri ="";
        try {
            
            while (!reader.isStartElement() && !reader.isEndElement())
                reader.next();

            
            if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
              java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                    "type");
              if (fullTypeName!=null){
                java.lang.String nsPrefix = null;
                if (fullTypeName.indexOf(":") > -1){
                    nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                }
                nsPrefix = nsPrefix==null?"":nsPrefix;

                java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                
                        if (!"test".equals(type)){
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Test)ExtensionMapper.getTypeObject(
                                 nsUri,type,reader);
                          }
                    

              }
            

            }

            

            
            // Note all attributes that were handled. Used to differ normal attributes
            // from anyAttributes.
            java.util.Vector handledAttributes = new java.util.Vector();
            

            
                
                reader.next();
              
                        while (!reader.isStartElement() && !reader.isEndElement())
                            reader.next();
                        
                            if (reader.isStartElement())
                            // A start element we are not expecting indicates a trailing invalid property
                            throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                        



        } catch (javax.xml.stream.XMLStreamException e) {
            throw new java.lang.Exception(e);
        }

        return object;
    }

    }//end of factory class

    

    }
       

    public static class TestResponse
    implements org.apache.axis2.databinding.ADBBean{
    
            public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            url,
            "testResponse",
            "ns1");

        

                    /**
                    * field for TestReturn
                    */

                    
                                protected java.lang.String localTestReturn ;
                            

                       /**
                       * Auto generated getter method
                       * @return java.lang.String
                       */
                       public  java.lang.String getTestReturn(){
                           return localTestReturn;
                       }

                       
                    
                        /**
                           * Auto generated setter method
                           * @param param TestReturn
                           */
                           public void setTestReturn(java.lang.String param){
                        
                                        this.localTestReturn=param;
                                   

                           }
                        

 
 
    /**
    *
    * @param parentQName
    * @param factory
    * @return org.apache.axiom.om.OMElement
    */
   public org.apache.axiom.om.OMElement getOMElement (
           final javax.xml.namespace.QName parentQName,
           final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


    
           org.apache.axiom.om.OMDataSource dataSource =
                   new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME);
           return factory.createOMElement(dataSource,MY_QNAME);
        
    }

     public void serialize(final javax.xml.namespace.QName parentQName,
                                   javax.xml.stream.XMLStreamWriter xmlWriter)
                            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                       serialize(parentQName,xmlWriter,false);
     }

     public void serialize(final javax.xml.namespace.QName parentQName,
                           javax.xml.stream.XMLStreamWriter xmlWriter,
                           boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        
            


            java.lang.String prefix = null;
            java.lang.String namespace = null;
            

                prefix = parentQName.getPrefix();
                namespace = parentQName.getNamespaceURI();
                writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
            
              if (serializeType){
           

               java.lang.String namespacePrefix = registerPrefix(xmlWriter,url);
               if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       namespacePrefix+":testResponse",
                       xmlWriter);
               } else {
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       "testResponse",
                       xmlWriter);
               }

           
               }
           
                                namespace = "";
                                writeStartElement(null, namespace, "testReturn", xmlWriter);
                         

                                      if (localTestReturn==null){
                                          // write the nil attribute
                                          
                                                 writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                              
                                      }else{

                                    
                                               xmlWriter.writeCharacters(localTestReturn);
                                        
                                      }
                                
                               xmlWriter.writeEndElement();
                         
                xmlWriter.writeEndElement();
           

    }

    private static java.lang.String generatePrefix(java.lang.String namespace) {
        if(namespace.equals(url)){
            return "ns1";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                   javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
        if (writerPrefix != null) {
            xmlWriter.writeStartElement(namespace, localPart);
        } else {
            if (namespace.length() == 0) {
                prefix = "";
            } else if (prefix == null) {
                prefix = generatePrefix(namespace);
            }

            xmlWriter.writeStartElement(prefix, localPart, namespace);
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
    }
    
    /**
     * Util method to write an attribute with the ns prefix
     */
    private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        xmlWriter.writeAttribute(namespace,attName,attValue);
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName,attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace,attName,attValue);
        }
    }


       /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                         javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }
    /**
     *  method to handle Qnames
     */

    private void writeQName(javax.xml.namespace.QName qname,
                            javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix,namespaceURI);
            }

            if (prefix.trim().length() > 0){
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(javax.xml.namespace.QName[] qnames,
                             javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
            java.lang.String namespaceURI = null;
            java.lang.String prefix = null;

            for (int i = 0; i < qnames.length; i++) {
                if (i > 0) {
                    stringToWrite.append(" ");
                }
                namespaceURI = qnames[i].getNamespaceURI();
                if (namespaceURI != null) {
                    prefix = xmlWriter.getPrefix(namespaceURI);
                    if ((prefix == null) || (prefix.length() == 0)) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix,namespaceURI);
                    }

                    if (prefix.trim().length() > 0){
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                } else {
                    stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                }
            }
            xmlWriter.writeCharacters(stringToWrite.toString());
        }

    }


    /**
     * Register a namespace prefix
     */
    private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);
        if (prefix == null) {
            prefix = generatePrefix(namespace);
            javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
            while (true) {
                java.lang.String uri = nsContext.getNamespaceURI(prefix);
                if (uri == null || uri.length() == 0) {
                    break;
                }
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
            }
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        return prefix;
    }



    /**
    * databinding method to get an XML representation of this object
    *
    */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException{


    
             java.util.ArrayList elementList = new java.util.ArrayList();
             java.util.ArrayList attribList = new java.util.ArrayList();

            
                                  elementList.add(new javax.xml.namespace.QName("",
                                                                  "testReturn"));
                             
                                     elementList.add(localTestReturn==null?null:
                                     org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTestReturn));
                                

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
        
        

    }



 /**
  *  Factory class that keeps the parse method
  */
public static class Factory{

    
    

    /**
    * static method to create the object
    * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
    *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
    * Postcondition: If this object is an element, the reader is positioned at its end element
    *                If this object is a complex type, the reader is positioned at the end element of its outer element
    */
    public static TestResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
        TestResponse object =
            new TestResponse();

        int event;
        java.lang.String nillableValue = null;
        java.lang.String prefix ="";
        java.lang.String namespaceuri ="";
        try {
            
            while (!reader.isStartElement() && !reader.isEndElement())
                reader.next();

            
            if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
              java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                    "type");
              if (fullTypeName!=null){
                java.lang.String nsPrefix = null;
                if (fullTypeName.indexOf(":") > -1){
                    nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                }
                nsPrefix = nsPrefix==null?"":nsPrefix;

                java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                
                        if (!"testResponse".equals(type)){
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (TestResponse)ExtensionMapper.getTypeObject(
                                 nsUri,type,reader);
                          }
                    

              }
            

            }

            

            
            // Note all attributes that were handled. Used to differ normal attributes
            // from anyAttributes.
            java.util.Vector handledAttributes = new java.util.Vector();
            

            
                
                reader.next();
            
                                
                                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            
                                if (reader.isStartElement() && new javax.xml.namespace.QName("","testReturn").equals(reader.getName())){
                            
                                   nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                   if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                

                                java.lang.String content = reader.getElementText();
                                
                                          object.setTestReturn(
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                        
                                   } else {
                                       
                                       
                                       reader.getElementText(); // throw away text nodes if any.
                                   }
                                  
                                    reader.next();
                                
                          }  // End of if for expected property start element
                            
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            }
                          
                        while (!reader.isStartElement() && !reader.isEndElement())
                            reader.next();
                        
                            if (reader.isStartElement())
                            // A start element we are not expecting indicates a trailing invalid property
                            throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                        



        } catch (javax.xml.stream.XMLStreamException e) {
            throw new java.lang.Exception(e);
        }

        return object;
    }

    }//end of factory class

    

    }
       

    public static class OpcodeWithFlags
    implements org.apache.axis2.databinding.ADBBean{
    
            public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            url,
            "opcodeWithFlags",
            "ns1");

        

                    /**
                    * field for Opcode
                    */

                    
                                protected java.lang.String localOpcode ;
                            

                       /**
                       * Auto generated getter method
                       * @return java.lang.String
                       */
                       public  java.lang.String getOpcode(){
                           return localOpcode;
                       }

                       
                    
                        /**
                           * Auto generated setter method
                           * @param param Opcode
                           */
                           public void setOpcode(java.lang.String param){
                        
                                        this.localOpcode=param;
                                   

                           }
                        

                    /**
                    * field for Flags
                    */

                    
                                protected int localFlags ;
                            

                       /**
                       * Auto generated getter method
                       * @return int
                       */
                       public  int getFlags(){
                           return localFlags;
                       }

                       
                    
                        /**
                           * Auto generated setter method
                           * @param param Flags
                           */
                           public void setFlags(int param){
                        
                                        this.localFlags=param;
                                   

                           }
                        

                    /**
                    * field for InputXML
                    */

                    
                                protected java.lang.String localInputXML ;
                            

                       /**
                       * Auto generated getter method
                       * @return java.lang.String
                       */
                       public  java.lang.String getInputXML(){
                           return localInputXML;
                       }

                       
                    
                        /**
                           * Auto generated setter method
                           * @param param InputXML
                           */
                           public void setInputXML(java.lang.String param){
                        
                                        this.localInputXML=param;
                                   

                           }
                        

                    /**
                    * field for M_SchemaFile
                    */

                    
                                protected java.lang.String localM_SchemaFile ;
                            

                       /**
                       * Auto generated getter method
                       * @return java.lang.String
                       */
                       public  java.lang.String getM_SchemaFile(){
                           return localM_SchemaFile;
                       }

                       
                    
                        /**
                           * Auto generated setter method
                           * @param param M_SchemaFile
                           */
                           public void setM_SchemaFile(java.lang.String param){
                        
                                        this.localM_SchemaFile=param;
                                   

                           }
                        

 
 
    /**
    *
    * @param parentQName
    * @param factory
    * @return org.apache.axiom.om.OMElement
    */
   public org.apache.axiom.om.OMElement getOMElement (
           final javax.xml.namespace.QName parentQName,
           final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


    
           org.apache.axiom.om.OMDataSource dataSource =
                   new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME);
           return factory.createOMElement(dataSource,MY_QNAME);
        
    }

     public void serialize(final javax.xml.namespace.QName parentQName,
                                   javax.xml.stream.XMLStreamWriter xmlWriter)
                            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                       serialize(parentQName,xmlWriter,false);
     }

     public void serialize(final javax.xml.namespace.QName parentQName,
                           javax.xml.stream.XMLStreamWriter xmlWriter,
                           boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        
            


            java.lang.String prefix = null;
            java.lang.String namespace = null;
            

                prefix = parentQName.getPrefix();
                namespace = parentQName.getNamespaceURI();
                writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
            
              if (serializeType){
           

               java.lang.String namespacePrefix = registerPrefix(xmlWriter,url);
               if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       namespacePrefix+":opcodeWithFlags",
                       xmlWriter);
               } else {
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       "opcodeWithFlags",
                       xmlWriter);
               }

           
               }
           
                                namespace = "";
                                writeStartElement(null, namespace, "opcode", xmlWriter);
                         

                                      if (localOpcode==null){
                                          // write the nil attribute
                                          
                                                 writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                              
                                      }else{

                                    
                                               xmlWriter.writeCharacters(localOpcode);
                                        
                                      }
                                
                               xmlWriter.writeEndElement();
                         
                                namespace = "";
                                writeStartElement(null, namespace, "flags", xmlWriter);
                         
                                           if (localFlags==java.lang.Integer.MIN_VALUE) {
                                       
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                           } else {
                                                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFlags));
                                           }
                                
                               xmlWriter.writeEndElement();
                         
                                namespace = "";
                                writeStartElement(null, namespace, "inputXML", xmlWriter);
                         

                                      if (localInputXML==null){
                                          // write the nil attribute
                                          
                                                 writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                              
                                      }else{

                                    
                                               xmlWriter.writeCharacters(localInputXML);
                                        
                                      }
                                
                               xmlWriter.writeEndElement();
                         
                                namespace = "";
                                writeStartElement(null, namespace, "m_SchemaFile", xmlWriter);
                         

                                      if (localM_SchemaFile==null){
                                          // write the nil attribute
                                          
                                                 writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                              
                                      }else{

                                    
                                               xmlWriter.writeCharacters(localM_SchemaFile);
                                        
                                      }
                                
                               xmlWriter.writeEndElement();
                         
                xmlWriter.writeEndElement();
           

    }

    private static java.lang.String generatePrefix(java.lang.String namespace) {
        if(namespace.equals(url)){
            return "ns1";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                   javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
        if (writerPrefix != null) {
            xmlWriter.writeStartElement(namespace, localPart);
        } else {
            if (namespace.length() == 0) {
                prefix = "";
            } else if (prefix == null) {
                prefix = generatePrefix(namespace);
            }

            xmlWriter.writeStartElement(prefix, localPart, namespace);
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
    }
    
    /**
     * Util method to write an attribute with the ns prefix
     */
    private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        xmlWriter.writeAttribute(namespace,attName,attValue);
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName,attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace,attName,attValue);
        }
    }


       /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                         javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }
    /**
     *  method to handle Qnames
     */

    private void writeQName(javax.xml.namespace.QName qname,
                            javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix,namespaceURI);
            }

            if (prefix.trim().length() > 0){
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(javax.xml.namespace.QName[] qnames,
                             javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
            java.lang.String namespaceURI = null;
            java.lang.String prefix = null;

            for (int i = 0; i < qnames.length; i++) {
                if (i > 0) {
                    stringToWrite.append(" ");
                }
                namespaceURI = qnames[i].getNamespaceURI();
                if (namespaceURI != null) {
                    prefix = xmlWriter.getPrefix(namespaceURI);
                    if ((prefix == null) || (prefix.length() == 0)) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix,namespaceURI);
                    }

                    if (prefix.trim().length() > 0){
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                } else {
                    stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                }
            }
            xmlWriter.writeCharacters(stringToWrite.toString());
        }

    }


    /**
     * Register a namespace prefix
     */
    private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);
        if (prefix == null) {
            prefix = generatePrefix(namespace);
            javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
            while (true) {
                java.lang.String uri = nsContext.getNamespaceURI(prefix);
                if (uri == null || uri.length() == 0) {
                    break;
                }
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
            }
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        return prefix;
    }



    /**
    * databinding method to get an XML representation of this object
    *
    */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException{


    
             java.util.ArrayList elementList = new java.util.ArrayList();
             java.util.ArrayList attribList = new java.util.ArrayList();

            
                                  elementList.add(new javax.xml.namespace.QName("",
                                                                  "opcode"));
                             
                                     elementList.add(localOpcode==null?null:
                                     org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOpcode));
                                
                                  elementList.add(new javax.xml.namespace.QName("",
                                                                  "flags"));
                             
                            elementList.add(
                               org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFlags));
                        
                                  elementList.add(new javax.xml.namespace.QName("",
                                                                  "inputXML"));
                             
                                     elementList.add(localInputXML==null?null:
                                     org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInputXML));
                                
                                  elementList.add(new javax.xml.namespace.QName("",
                                                                  "m_SchemaFile"));
                             
                                     elementList.add(localM_SchemaFile==null?null:
                                     org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localM_SchemaFile));
                                

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
        
        

    }



 /**
  *  Factory class that keeps the parse method
  */
public static class Factory{

    
    

    /**
    * static method to create the object
    * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
    *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
    * Postcondition: If this object is an element, the reader is positioned at its end element
    *                If this object is a complex type, the reader is positioned at the end element of its outer element
    */
    public static OpcodeWithFlags parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
        OpcodeWithFlags object =
            new OpcodeWithFlags();

        int event;
        java.lang.String nillableValue = null;
        java.lang.String prefix ="";
        java.lang.String namespaceuri ="";
        try {
            
            while (!reader.isStartElement() && !reader.isEndElement())
                reader.next();

            
            if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
              java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                    "type");
              if (fullTypeName!=null){
                java.lang.String nsPrefix = null;
                if (fullTypeName.indexOf(":") > -1){
                    nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                }
                nsPrefix = nsPrefix==null?"":nsPrefix;

                java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                
                        if (!"opcodeWithFlags".equals(type)){
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (OpcodeWithFlags)ExtensionMapper.getTypeObject(
                                 nsUri,type,reader);
                          }
                    

              }
            

            }

            

            
            // Note all attributes that were handled. Used to differ normal attributes
            // from anyAttributes.
            java.util.Vector handledAttributes = new java.util.Vector();
            

            
                
                reader.next();
            
                                
                                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            
                                if (reader.isStartElement() && new javax.xml.namespace.QName("","opcode").equals(reader.getName())){
                            
                                   nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                   if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                

                                java.lang.String content = reader.getElementText();
                                
                                          object.setOpcode(
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                        
                                   } else {
                                       
                                       
                                       reader.getElementText(); // throw away text nodes if any.
                                   }
                                  
                                    reader.next();
                                
                          }  // End of if for expected property start element
                            
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            }
                        
                                
                                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            
                                if (reader.isStartElement() && new javax.xml.namespace.QName("","flags").equals(reader.getName())){
                            
                                   nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                   if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                

                                java.lang.String content = reader.getElementText();
                                
                                          object.setFlags(
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));
                                        
                                   } else {
                                       
                                       
                                               object.setFlags(java.lang.Integer.MIN_VALUE);
                                           
                                       reader.getElementText(); // throw away text nodes if any.
                                   }
                                  
                                    reader.next();
                                
                          }  // End of if for expected property start element
                            
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            }
                        
                                
                                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            
                                if (reader.isStartElement() && new javax.xml.namespace.QName("","inputXML").equals(reader.getName())){
                            
                                   nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                   if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                

                                java.lang.String content = reader.getElementText();
                                
                                          object.setInputXML(
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                        
                                   } else {
                                       
                                       
                                       reader.getElementText(); // throw away text nodes if any.
                                   }
                                  
                                    reader.next();
                                
                          }  // End of if for expected property start element
                            
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            }
                        
                                
                                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            
                                if (reader.isStartElement() && new javax.xml.namespace.QName("","m_SchemaFile").equals(reader.getName())){
                            
                                   nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                   if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                

                                java.lang.String content = reader.getElementText();
                                
                                          object.setM_SchemaFile(
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                        
                                   } else {
                                       
                                       
                                       reader.getElementText(); // throw away text nodes if any.
                                   }
                                  
                                    reader.next();
                                
                          }  // End of if for expected property start element
                            
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            }
                          
                        while (!reader.isStartElement() && !reader.isEndElement())
                            reader.next();
                        
                            if (reader.isStartElement())
                            // A start element we are not expecting indicates a trailing invalid property
                            throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                        



        } catch (javax.xml.stream.XMLStreamException e) {
            throw new java.lang.Exception(e);
        }

        return object;
    }

    }//end of factory class

    

    }
       

    public static class OpcodeWithFlagsResponse
    implements org.apache.axis2.databinding.ADBBean{
    
            public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            url,
            "opcodeWithFlagsResponse",
            "ns1");

        

                    /**
                    * field for OpcodeWithFlagsReturn
                    */

                    
                                protected java.lang.String localOpcodeWithFlagsReturn ;
                            

                       /**
                       * Auto generated getter method
                       * @return java.lang.String
                       */
                       public  java.lang.String getOpcodeWithFlagsReturn(){
                           return localOpcodeWithFlagsReturn;
                       }

                       
                    
                        /**
                           * Auto generated setter method
                           * @param param OpcodeWithFlagsReturn
                           */
                           public void setOpcodeWithFlagsReturn(java.lang.String param){
                        
                                        this.localOpcodeWithFlagsReturn=param;
                                   

                           }
                        

 
 
    /**
    *
    * @param parentQName
    * @param factory
    * @return org.apache.axiom.om.OMElement
    */
   public org.apache.axiom.om.OMElement getOMElement (
           final javax.xml.namespace.QName parentQName,
           final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


    
           org.apache.axiom.om.OMDataSource dataSource =
                   new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME);
           return factory.createOMElement(dataSource,MY_QNAME);
        
    }

     public void serialize(final javax.xml.namespace.QName parentQName,
                                   javax.xml.stream.XMLStreamWriter xmlWriter)
                            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                       serialize(parentQName,xmlWriter,false);
     }

     public void serialize(final javax.xml.namespace.QName parentQName,
                           javax.xml.stream.XMLStreamWriter xmlWriter,
                           boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        
            


            java.lang.String prefix = null;
            java.lang.String namespace = null;
            

                prefix = parentQName.getPrefix();
                namespace = parentQName.getNamespaceURI();
                writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
            
              if (serializeType){
           

               java.lang.String namespacePrefix = registerPrefix(xmlWriter,url);
               if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       namespacePrefix+":opcodeWithFlagsResponse",
                       xmlWriter);
               } else {
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       "opcodeWithFlagsResponse",
                       xmlWriter);
               }

           
               }
           
                                namespace = "";
                                writeStartElement(null, namespace, "opcodeWithFlagsReturn", xmlWriter);
                         

                                      if (localOpcodeWithFlagsReturn==null){
                                          // write the nil attribute
                                          
                                                 writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                              
                                      }else{

                                    
                                               xmlWriter.writeCharacters(localOpcodeWithFlagsReturn);
                                        
                                      }
                                
                               xmlWriter.writeEndElement();
                         
                xmlWriter.writeEndElement();
           

    }

    private static java.lang.String generatePrefix(java.lang.String namespace) {
        if(namespace.equals(url)){
            return "ns1";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                   javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
        if (writerPrefix != null) {
            xmlWriter.writeStartElement(namespace, localPart);
        } else {
            if (namespace.length() == 0) {
                prefix = "";
            } else if (prefix == null) {
                prefix = generatePrefix(namespace);
            }

            xmlWriter.writeStartElement(prefix, localPart, namespace);
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
    }
    
    /**
     * Util method to write an attribute with the ns prefix
     */
    private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        xmlWriter.writeAttribute(namespace,attName,attValue);
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName,attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace,attName,attValue);
        }
    }


       /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                         javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }
    /**
     *  method to handle Qnames
     */

    private void writeQName(javax.xml.namespace.QName qname,
                            javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix,namespaceURI);
            }

            if (prefix.trim().length() > 0){
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(javax.xml.namespace.QName[] qnames,
                             javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
            java.lang.String namespaceURI = null;
            java.lang.String prefix = null;

            for (int i = 0; i < qnames.length; i++) {
                if (i > 0) {
                    stringToWrite.append(" ");
                }
                namespaceURI = qnames[i].getNamespaceURI();
                if (namespaceURI != null) {
                    prefix = xmlWriter.getPrefix(namespaceURI);
                    if ((prefix == null) || (prefix.length() == 0)) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix,namespaceURI);
                    }

                    if (prefix.trim().length() > 0){
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                } else {
                    stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                }
            }
            xmlWriter.writeCharacters(stringToWrite.toString());
        }

    }


    /**
     * Register a namespace prefix
     */
    private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);
        if (prefix == null) {
            prefix = generatePrefix(namespace);
            javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
            while (true) {
                java.lang.String uri = nsContext.getNamespaceURI(prefix);
                if (uri == null || uri.length() == 0) {
                    break;
                }
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
            }
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        return prefix;
    }



    /**
    * databinding method to get an XML representation of this object
    *
    */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException{


    
             java.util.ArrayList elementList = new java.util.ArrayList();
             java.util.ArrayList attribList = new java.util.ArrayList();

            
                                  elementList.add(new javax.xml.namespace.QName("",
                                                                  "opcodeWithFlagsReturn"));
                             
                                     elementList.add(localOpcodeWithFlagsReturn==null?null:
                                     org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOpcodeWithFlagsReturn));
                                

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
        
        

    }



 /**
  *  Factory class that keeps the parse method
  */
public static class Factory{

    
    

    /**
    * static method to create the object
    * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
    *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
    * Postcondition: If this object is an element, the reader is positioned at its end element
    *                If this object is a complex type, the reader is positioned at the end element of its outer element
    */
    public static OpcodeWithFlagsResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
        OpcodeWithFlagsResponse object =
            new OpcodeWithFlagsResponse();

        int event;
        java.lang.String nillableValue = null;
        java.lang.String prefix ="";
        java.lang.String namespaceuri ="";
        try {
            
            while (!reader.isStartElement() && !reader.isEndElement())
                reader.next();

            
            if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
              java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                    "type");
              if (fullTypeName!=null){
                java.lang.String nsPrefix = null;
                if (fullTypeName.indexOf(":") > -1){
                    nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                }
                nsPrefix = nsPrefix==null?"":nsPrefix;

                java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                
                        if (!"opcodeWithFlagsResponse".equals(type)){
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (OpcodeWithFlagsResponse)ExtensionMapper.getTypeObject(
                                 nsUri,type,reader);
                          }
                    

              }
            

            }

            

            
            // Note all attributes that were handled. Used to differ normal attributes
            // from anyAttributes.
            java.util.Vector handledAttributes = new java.util.Vector();
            

            
                
                reader.next();
            
                                
                                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            
                                if (reader.isStartElement() && new javax.xml.namespace.QName("","opcodeWithFlagsReturn").equals(reader.getName())){
                            
                                   nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                   if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                

                                java.lang.String content = reader.getElementText();
                                
                                          object.setOpcodeWithFlagsReturn(
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                        
                                   } else {
                                       
                                       
                                       reader.getElementText(); // throw away text nodes if any.
                                   }
                                  
                                    reader.next();
                                
                          }  // End of if for expected property start element
                            
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            }
                          
                        while (!reader.isStartElement() && !reader.isEndElement())
                            reader.next();
                        
                            if (reader.isStartElement())
                            // A start element we are not expecting indicates a trailing invalid property
                            throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                        



        } catch (javax.xml.stream.XMLStreamException e) {
            throw new java.lang.Exception(e);
        }

        return object;
    }

    }//end of factory class

    

    }
       

    public static class Opcode
    implements org.apache.axis2.databinding.ADBBean{
    
            public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            url,
            "opcode",
            "ns1");

        

                    /**
                    * field for Opcode
                    */

                    
                                protected java.lang.String localOpcode ;
                            

                       /**
                       * Auto generated getter method
                       * @return java.lang.String
                       */
                       public  java.lang.String getOpcode(){
                           return localOpcode;
                       }

                       
                    
                        /**
                           * Auto generated setter method
                           * @param param Opcode
                           */
                           public void setOpcode(java.lang.String param){
                        
                                        this.localOpcode=param;
                                   

                           }
                        

                    /**
                    * field for InputXML
                    */

                    
                                protected java.lang.String localInputXML ;
                            

                       /**
                       * Auto generated getter method
                       * @return java.lang.String
                       */
                       public  java.lang.String getInputXML(){
                           return localInputXML;
                       }

                       
                    
                        /**
                           * Auto generated setter method
                           * @param param InputXML
                           */
                           public void setInputXML(java.lang.String param){
                        
                                        this.localInputXML=param;
                                   

                           }
                        

                    /**
                    * field for M_SchemaFile
                    */

                    
                                protected java.lang.String localM_SchemaFile ;
                            

                       /**
                       * Auto generated getter method
                       * @return java.lang.String
                       */
                       public  java.lang.String getM_SchemaFile(){
                           return localM_SchemaFile;
                       }

                       
                    
                        /**
                           * Auto generated setter method
                           * @param param M_SchemaFile
                           */
                           public void setM_SchemaFile(java.lang.String param){
                        
                                        this.localM_SchemaFile=param;
                                   

                           }
                        

 
 
    /**
    *
    * @param parentQName
    * @param factory
    * @return org.apache.axiom.om.OMElement
    */
   public org.apache.axiom.om.OMElement getOMElement (
           final javax.xml.namespace.QName parentQName,
           final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


    
           org.apache.axiom.om.OMDataSource dataSource =
                   new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME);
           return factory.createOMElement(dataSource,MY_QNAME);
        
    }

     public void serialize(final javax.xml.namespace.QName parentQName,
                                   javax.xml.stream.XMLStreamWriter xmlWriter)
                            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                       serialize(parentQName,xmlWriter,false);
     }

     public void serialize(final javax.xml.namespace.QName parentQName,
                           javax.xml.stream.XMLStreamWriter xmlWriter,
                           boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        
            


            java.lang.String prefix = null;
            java.lang.String namespace = null;
            

                prefix = parentQName.getPrefix();
                namespace = parentQName.getNamespaceURI();
                writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
            
              if (serializeType){
           

               java.lang.String namespacePrefix = registerPrefix(xmlWriter,url);
               if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       namespacePrefix+":opcode",
                       xmlWriter);
               } else {
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       "opcode",
                       xmlWriter);
               }

           
               }
           
                                namespace = "";
                                writeStartElement(null, namespace, "opcode", xmlWriter);
                         

                                      if (localOpcode==null){
                                          // write the nil attribute
                                          
                                                 writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                              
                                      }else{

                                    
                                               xmlWriter.writeCharacters(localOpcode);
                                        
                                      }
                                
                               xmlWriter.writeEndElement();
                         
                                namespace = "";
                                writeStartElement(null, namespace, "inputXML", xmlWriter);
                         

                                      if (localInputXML==null){
                                          // write the nil attribute
                                          
                                                 writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                              
                                      }else{

                                    
                                               xmlWriter.writeCharacters(localInputXML);
                                        
                                      }
                                
                               xmlWriter.writeEndElement();
                         
                                namespace = "";
                                writeStartElement(null, namespace, "m_SchemaFile", xmlWriter);
                         

                                      if (localM_SchemaFile==null){
                                          // write the nil attribute
                                          
                                                 writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                              
                                      }else{

                                    
                                               xmlWriter.writeCharacters(localM_SchemaFile);
                                        
                                      }
                                
                               xmlWriter.writeEndElement();
                         
                xmlWriter.writeEndElement();
           

    }

    private static java.lang.String generatePrefix(java.lang.String namespace) {
        if(namespace.equals(url)){
            return "ns1";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                   javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
        if (writerPrefix != null) {
            xmlWriter.writeStartElement(namespace, localPart);
        } else {
            if (namespace.length() == 0) {
                prefix = "";
            } else if (prefix == null) {
                prefix = generatePrefix(namespace);
            }

            xmlWriter.writeStartElement(prefix, localPart, namespace);
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
    }
    
    /**
     * Util method to write an attribute with the ns prefix
     */
    private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        xmlWriter.writeAttribute(namespace,attName,attValue);
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName,attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace,attName,attValue);
        }
    }


       /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                         javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }
    /**
     *  method to handle Qnames
     */

    private void writeQName(javax.xml.namespace.QName qname,
                            javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix,namespaceURI);
            }

            if (prefix.trim().length() > 0){
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(javax.xml.namespace.QName[] qnames,
                             javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
            java.lang.String namespaceURI = null;
            java.lang.String prefix = null;

            for (int i = 0; i < qnames.length; i++) {
                if (i > 0) {
                    stringToWrite.append(" ");
                }
                namespaceURI = qnames[i].getNamespaceURI();
                if (namespaceURI != null) {
                    prefix = xmlWriter.getPrefix(namespaceURI);
                    if ((prefix == null) || (prefix.length() == 0)) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix,namespaceURI);
                    }

                    if (prefix.trim().length() > 0){
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                } else {
                    stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                }
            }
            xmlWriter.writeCharacters(stringToWrite.toString());
        }

    }


    /**
     * Register a namespace prefix
     */
    private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);
        if (prefix == null) {
            prefix = generatePrefix(namespace);
            javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
            while (true) {
                java.lang.String uri = nsContext.getNamespaceURI(prefix);
                if (uri == null || uri.length() == 0) {
                    break;
                }
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
            }
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        return prefix;
    }



    /**
    * databinding method to get an XML representation of this object
    *
    */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException{


    
             java.util.ArrayList elementList = new java.util.ArrayList();
             java.util.ArrayList attribList = new java.util.ArrayList();

            
                                  elementList.add(new javax.xml.namespace.QName("",
                                                                  "opcode"));
                             
                                     elementList.add(localOpcode==null?null:
                                     org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOpcode));
                                
                                  elementList.add(new javax.xml.namespace.QName("",
                                                                  "inputXML"));
                             
                                     elementList.add(localInputXML==null?null:
                                     org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInputXML));
                                
                                  elementList.add(new javax.xml.namespace.QName("",
                                                                  "m_SchemaFile"));
                             
                                     elementList.add(localM_SchemaFile==null?null:
                                     org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localM_SchemaFile));
                                

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
        
        

    }



 /**
  *  Factory class that keeps the parse method
  */
public static class Factory{

    
    

    /**
    * static method to create the object
    * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
    *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
    * Postcondition: If this object is an element, the reader is positioned at its end element
    *                If this object is a complex type, the reader is positioned at the end element of its outer element
    */
    public static Opcode parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
        Opcode object =
            new Opcode();

        int event;
        java.lang.String nillableValue = null;
        java.lang.String prefix ="";
        java.lang.String namespaceuri ="";
        try {
            
            while (!reader.isStartElement() && !reader.isEndElement())
                reader.next();

            
            if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
              java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                    "type");
              if (fullTypeName!=null){
                java.lang.String nsPrefix = null;
                if (fullTypeName.indexOf(":") > -1){
                    nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                }
                nsPrefix = nsPrefix==null?"":nsPrefix;

                java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                
                        if (!"opcode".equals(type)){
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Opcode)ExtensionMapper.getTypeObject(
                                 nsUri,type,reader);
                          }
                    

              }
            

            }

            

            
            // Note all attributes that were handled. Used to differ normal attributes
            // from anyAttributes.
            java.util.Vector handledAttributes = new java.util.Vector();
            

            
                
                reader.next();
            
                                
                                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            
                                if (reader.isStartElement() && new javax.xml.namespace.QName("","opcode").equals(reader.getName())){
                            
                                   nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                   if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                

                                java.lang.String content = reader.getElementText();
                                
                                          object.setOpcode(
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                        
                                   } else {
                                       
                                       
                                       reader.getElementText(); // throw away text nodes if any.
                                   }
                                  
                                    reader.next();
                                
                          }  // End of if for expected property start element
                            
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            }
                        
                                
                                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            
                                if (reader.isStartElement() && new javax.xml.namespace.QName("","inputXML").equals(reader.getName())){
                            
                                   nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                   if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                

                                java.lang.String content = reader.getElementText();
                                
                                          object.setInputXML(
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                        
                                   } else {
                                       
                                       
                                       reader.getElementText(); // throw away text nodes if any.
                                   }
                                  
                                    reader.next();
                                
                          }  // End of if for expected property start element
                            
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            }
                        
                                
                                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            
                                if (reader.isStartElement() && new javax.xml.namespace.QName("","m_SchemaFile").equals(reader.getName())){
                            
                                   nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                   if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                

                                java.lang.String content = reader.getElementText();
                                
                                          object.setM_SchemaFile(
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                        
                                   } else {
                                       
                                       
                                       reader.getElementText(); // throw away text nodes if any.
                                   }
                                  
                                    reader.next();
                                
                          }  // End of if for expected property start element
                            
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            }
                          
                        while (!reader.isStartElement() && !reader.isEndElement())
                            reader.next();
                        
                            if (reader.isStartElement())
                            // A start element we are not expecting indicates a trailing invalid property
                            throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                        



        } catch (javax.xml.stream.XMLStreamException e) {
            throw new java.lang.Exception(e);
        }

        return object;
    }

    }//end of factory class

    

    }
       

    public static class OpcodeResponse
    implements org.apache.axis2.databinding.ADBBean{
    
            public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            url,
            "opcodeResponse",
            "ns1");

        

                    /**
                    * field for OpcodeReturn
                    */

                    
                                protected java.lang.String localOpcodeReturn ;
                            

                       /**
                       * Auto generated getter method
                       * @return java.lang.String
                       */
                       public  java.lang.String getOpcodeReturn(){
                           return localOpcodeReturn;
                       }

                       
                    
                        /**
                           * Auto generated setter method
                           * @param param OpcodeReturn
                           */
                           public void setOpcodeReturn(java.lang.String param){
                        
                                        this.localOpcodeReturn=param;
                                   

                           }
                        

 
 
    /**
    *
    * @param parentQName
    * @param factory
    * @return org.apache.axiom.om.OMElement
    */
   public org.apache.axiom.om.OMElement getOMElement (
           final javax.xml.namespace.QName parentQName,
           final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


    
           org.apache.axiom.om.OMDataSource dataSource =
                   new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME);
           return factory.createOMElement(dataSource,MY_QNAME);
        
    }

     public void serialize(final javax.xml.namespace.QName parentQName,
                                   javax.xml.stream.XMLStreamWriter xmlWriter)
                            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                       serialize(parentQName,xmlWriter,false);
     }

     public void serialize(final javax.xml.namespace.QName parentQName,
                           javax.xml.stream.XMLStreamWriter xmlWriter,
                           boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        
            


            java.lang.String prefix = null;
            java.lang.String namespace = null;
            

                prefix = parentQName.getPrefix();
                namespace = parentQName.getNamespaceURI();
                writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
            
              if (serializeType){
           

               java.lang.String namespacePrefix = registerPrefix(xmlWriter,url);
               if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       namespacePrefix+":opcodeResponse",
                       xmlWriter);
               } else {
                   writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                       "opcodeResponse",
                       xmlWriter);
               }

           
               }
           
                                namespace = "";
                                writeStartElement(null, namespace, "opcodeReturn", xmlWriter);
                         

                                      if (localOpcodeReturn==null){
                                          // write the nil attribute
                                          
                                                 writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                              
                                      }else{

                                    
                                               xmlWriter.writeCharacters(localOpcodeReturn);
                                        
                                      }
                                
                               xmlWriter.writeEndElement();
                         
                xmlWriter.writeEndElement();
           

    }

    private static java.lang.String generatePrefix(java.lang.String namespace) {
        if(namespace.equals(url)){
            return "ns1";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                   javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
        if (writerPrefix != null) {
            xmlWriter.writeStartElement(namespace, localPart);
        } else {
            if (namespace.length() == 0) {
                prefix = "";
            } else if (prefix == null) {
                prefix = generatePrefix(namespace);
            }

            xmlWriter.writeStartElement(prefix, localPart, namespace);
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
    }
    
    /**
     * Util method to write an attribute with the ns prefix
     */
    private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        xmlWriter.writeAttribute(namespace,attName,attValue);
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName,attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace,attName,attValue);
        }
    }


       /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                         javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            } else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }
    /**
     *  method to handle Qnames
     */

    private void writeQName(javax.xml.namespace.QName qname,
                            javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix,namespaceURI);
            }

            if (prefix.trim().length() > 0){
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(javax.xml.namespace.QName[] qnames,
                             javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
            java.lang.String namespaceURI = null;
            java.lang.String prefix = null;

            for (int i = 0; i < qnames.length; i++) {
                if (i > 0) {
                    stringToWrite.append(" ");
                }
                namespaceURI = qnames[i].getNamespaceURI();
                if (namespaceURI != null) {
                    prefix = xmlWriter.getPrefix(namespaceURI);
                    if ((prefix == null) || (prefix.length() == 0)) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix,namespaceURI);
                    }

                    if (prefix.trim().length() > 0){
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                } else {
                    stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                }
            }
            xmlWriter.writeCharacters(stringToWrite.toString());
        }

    }


    /**
     * Register a namespace prefix
     */
    private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);
        if (prefix == null) {
            prefix = generatePrefix(namespace);
            javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
            while (true) {
                java.lang.String uri = nsContext.getNamespaceURI(prefix);
                if (uri == null || uri.length() == 0) {
                    break;
                }
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
            }
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        return prefix;
    }



    /**
    * databinding method to get an XML representation of this object
    *
    */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException{


    
             java.util.ArrayList elementList = new java.util.ArrayList();
             java.util.ArrayList attribList = new java.util.ArrayList();

            
                                  elementList.add(new javax.xml.namespace.QName("",
                                                                  "opcodeReturn"));
                             
                                     elementList.add(localOpcodeReturn==null?null:
                                     org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOpcodeReturn));
                                

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
        
        

    }



 /**
  *  Factory class that keeps the parse method
  */
public static class Factory{

    
    

    /**
    * static method to create the object
    * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
    *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
    * Postcondition: If this object is an element, the reader is positioned at its end element
    *                If this object is a complex type, the reader is positioned at the end element of its outer element
    */
    public static OpcodeResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
        OpcodeResponse object =
            new OpcodeResponse();

        int event;
        java.lang.String nillableValue = null;
        java.lang.String prefix ="";
        java.lang.String namespaceuri ="";
        try {
            
            while (!reader.isStartElement() && !reader.isEndElement())
                reader.next();

            
            if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
              java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                    "type");
              if (fullTypeName!=null){
                java.lang.String nsPrefix = null;
                if (fullTypeName.indexOf(":") > -1){
                    nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                }
                nsPrefix = nsPrefix==null?"":nsPrefix;

                java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                
                        if (!"opcodeResponse".equals(type)){
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (OpcodeResponse)ExtensionMapper.getTypeObject(
                                 nsUri,type,reader);
                          }
                    

              }
            

            }

            

            
            // Note all attributes that were handled. Used to differ normal attributes
            // from anyAttributes.
            java.util.Vector handledAttributes = new java.util.Vector();
            

            
                
                reader.next();
            
                                
                                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                            
                                if (reader.isStartElement() && new javax.xml.namespace.QName("","opcodeReturn").equals(reader.getName())){
                            
                                   nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                   if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                

                                java.lang.String content = reader.getElementText();
                                
                                          object.setOpcodeReturn(
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                        
                                   } else {
                                       
                                       
                                       reader.getElementText(); // throw away text nodes if any.
                                   }
                                  
                                    reader.next();
                                
                          }  // End of if for expected property start element
                            
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            }
                          
                        while (!reader.isStartElement() && !reader.isEndElement())
                            reader.next();
                        
                            if (reader.isStartElement())
                            // A start element we are not expecting indicates a trailing invalid property
                            throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                        



        } catch (javax.xml.stream.XMLStreamException e) {
            throw new java.lang.Exception(e);
        }

        return object;
    }

    }//end of factory class

    

    }
       

        private  org.apache.axiom.om.OMElement  toOM(InfranetWebServiceServiceStub.OpcodeWithFlags param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(InfranetWebServiceServiceStub.OpcodeWithFlags.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
        private  org.apache.axiom.om.OMElement  toOM(InfranetWebServiceServiceStub.OpcodeWithFlagsResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(InfranetWebServiceServiceStub.OpcodeWithFlagsResponse.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
        private  org.apache.axiom.om.OMElement  toOM(InfranetWebServiceServiceStub.Test param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(InfranetWebServiceServiceStub.Test.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
        private  org.apache.axiom.om.OMElement  toOM(InfranetWebServiceServiceStub.TestResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(InfranetWebServiceServiceStub.TestResponse.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
        private  org.apache.axiom.om.OMElement  toOM(InfranetWebServiceServiceStub.Opcode param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(InfranetWebServiceServiceStub.Opcode.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
        private  org.apache.axiom.om.OMElement  toOM(InfranetWebServiceServiceStub.OpcodeResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(InfranetWebServiceServiceStub.OpcodeResponse.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
                                
                                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, InfranetWebServiceServiceStub.OpcodeWithFlags param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                                    throws org.apache.axis2.AxisFault{

                                         
                                                try{

                                                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                        emptyEnvelope.getBody().addChild(param.getOMElement(InfranetWebServiceServiceStub.OpcodeWithFlags.MY_QNAME,factory));
                                                        return emptyEnvelope;
                                                    } catch(org.apache.axis2.databinding.ADBException e){
                                                        throw org.apache.axis2.AxisFault.makeFault(e);
                                                    }
                                            

                                    }
                            
                         
                         /* methods to provide back word compatibility */

                         
                                
                                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, InfranetWebServiceServiceStub.Test param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                                    throws org.apache.axis2.AxisFault{

                                         
                                                try{

                                                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                        emptyEnvelope.getBody().addChild(param.getOMElement(InfranetWebServiceServiceStub.Test.MY_QNAME,factory));
                                                        return emptyEnvelope;
                                                    } catch(org.apache.axis2.databinding.ADBException e){
                                                        throw org.apache.axis2.AxisFault.makeFault(e);
                                                    }
                                            

                                    }
                            
                         
                         /* methods to provide back word compatibility */

                         
                                
                                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, InfranetWebServiceServiceStub.Opcode param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                                    throws org.apache.axis2.AxisFault{

                                         
                                                try{

                                                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                        emptyEnvelope.getBody().addChild(param.getOMElement(InfranetWebServiceServiceStub.Opcode.MY_QNAME,factory));
                                                        return emptyEnvelope;
                                                    } catch(org.apache.axis2.databinding.ADBException e){
                                                        throw org.apache.axis2.AxisFault.makeFault(e);
                                                    }
                                            

                                    }
                            
                         
                         /* methods to provide back word compatibility */

                         


    /**
    *  get the default envelope
    */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
    return factory.getDefaultEnvelope();
    }


    private  java.lang.Object fromOM(
    org.apache.axiom.om.OMElement param,
    java.lang.Class type,
    java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault{

    try {
    
            if (InfranetWebServiceServiceStub.Opcode.class.equals(type)){
            
                    return InfranetWebServiceServiceStub.Opcode.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
        
            if (InfranetWebServiceServiceStub.OpcodeResponse.class.equals(type)){
            
                    return InfranetWebServiceServiceStub.OpcodeResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
        
            if (InfranetWebServiceServiceStub.OpcodeWithFlags.class.equals(type)){
            
                    return InfranetWebServiceServiceStub.OpcodeWithFlags.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
        
            if (InfranetWebServiceServiceStub.OpcodeWithFlagsResponse.class.equals(type)){
            
                    return InfranetWebServiceServiceStub.OpcodeWithFlagsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
        
            if (InfranetWebServiceServiceStub.Test.class.equals(type)){
            
                    return InfranetWebServiceServiceStub.Test.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
        
            if (InfranetWebServiceServiceStub.TestResponse.class.equals(type)){
            
                    return InfranetWebServiceServiceStub.TestResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
        
    } catch (java.lang.Exception e) {
    throw org.apache.axis2.AxisFault.makeFault(e);
    }
       return null;
    }





}
