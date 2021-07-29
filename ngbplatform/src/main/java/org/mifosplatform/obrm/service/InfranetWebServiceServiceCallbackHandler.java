package org.mifosplatform.obrm.service;

public abstract class InfranetWebServiceServiceCallbackHandler {




    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public InfranetWebServiceServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public InfranetWebServiceServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for opcodeWithFlags method
            * override this method for handling normal response from opcodeWithFlags operation
            */
           public void receiveResultopcodeWithFlags(
                    InfranetWebServiceServiceStub.OpcodeWithFlagsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from opcodeWithFlags operation
           */
            public void receiveErroropcodeWithFlags(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for test method
            * override this method for handling normal response from test operation
            */
           public void receiveResulttest(
                    InfranetWebServiceServiceStub.TestResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from test operation
           */
            public void receiveErrortest(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for opcode method
            * override this method for handling normal response from opcode operation
            */
           public void receiveResultopcode(
                    InfranetWebServiceServiceStub.OpcodeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from opcode operation
           */
            public void receiveErroropcode(java.lang.Exception e) {
            }
                


    
}
