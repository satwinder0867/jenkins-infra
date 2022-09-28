def call(){
    withCredentials([file(credentialsId: '	LOGGING_INVENTORY', variable: 'FILE')]) {
        sh 'cp $FILE $WORKSPACE/deploy/clusterLoggingInventory'
    }
}