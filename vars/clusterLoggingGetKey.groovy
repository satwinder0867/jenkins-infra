def call(){
    withCredentials([file(credentialsId: 'CLUSTER_LOGGING_PVT_KEY', variable: 'FILE')]) {
        sh 'cp $FILE $WORKSPACE/deploy/id_rsa'
        sh 'chmod 600 $WORKSPACE/deploy/id_rsa'
    }
}