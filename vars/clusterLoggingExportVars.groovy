def call(){
    script {
        ansiColor('xterm') {
            echo ""
        }
        env.CLFCLEANUP = "true"
        try {
            sh '''
                
                echo "export CHANNEL=${LoggingVersion}" >  logging_vars.sh
                echo "export CLUSTERLOGGING_INDEX=${ClusterLogging_index}" >> logging_vars.sh
                echo "export ELASTICSEARCH_INDEX=${Elasticsearch_index}" >> logging_vars.sh
                echo "export LOKIOPERATOR_INDEX=${LokiOperator_index}" >> logging_vars.sh
                echo "export LOGCOLLECTORTYPE=${LogCollectorType}" >> logging_vars.sh
                echo "export OCPVERSION=${OCPVersion}" >> logging_vars.sh

                cd ${WORKSPACE}/deploy               
                ssh -o 'StrictHostKeyChecking no' -i id_rsa root@${OCP_IP} "rm -rf /root/ocp_ansible_validation; git clone https://satwsin1:${GITHUBTOKEN}@github.ibm.com/redstack-power/ocp_ansible_validation.git"
                scp -r -o 'StrictHostKeyChecking no' -i id_rsa ${WORKSPACE}/deploy/ocp_cluster_logging_vars.yml root@${OCP_IP}:/root/ocp_ansible_validation/examples/
                scp -r -o 'StrictHostKeyChecking no' -i id_rsa ${WORKSPACE}/logging_vars.sh root@${OCP_IP}:/root/ 
                scp -o 'StrictHostKeyChecking no' -i id_rsa ${WORKSPACE}/deploy/cl_inventory root@${OCP_IP}:/root/ocp_ansible_validation/
                scp -r -o 'StrictHostKeyChecking no' -i id_rsa ${WORKSPACE}/scripts/update-logging-vars.sh root@${OCP_IP}:/root/           
                ssh -o 'StrictHostKeyChecking no' -i id_rsa root@${OCP_IP} "chmod 755 update-logging-vars.sh; ./update-logging-vars.sh"  
                scp -r -o 'StrictHostKeyChecking no' -i id_rsa root@${OCP_IP}:/root/cluster_logging_output.txt ${WORKSPACE}/
  
            '''
        }
        catch (err) {
            echo 'Error ! Exporting of environment variables failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}

