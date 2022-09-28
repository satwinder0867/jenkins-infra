def call(){
    withCredentials([file(credentialsId: 'CLUSTER_LOGGING_VARS', variable: 'FILE')]) {
        sh 'cp $FILE $WORKSPACE/deploy/ocp_cluster_logging_vars.yml'
        
        //sh 'yes | cp -r $FILE /root/clusterLoggingVars.yaml'
    
    }

    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                echo "export CHANNEL=${LOGGINGVERSION}" >  logging_vars.sh
                echo "export CHANNEL=${LoggingVersion}" >>  logging_vars.sh
                echo "export CLUSTERLOGGING_INDEX=${CLUSTERLOGGING_INDEX}" >> logging_vars.sh
                echo "export ELASTICSEARCH_INDEX=${ELASTICSEARCH_INDEX}" >> logging_vars.sh
                echo "export LOKIOPERATOR_INDEX=${LOKIOPERATOR_INDEX}" >> logging_vars.sh
                echo "export LOGCOLLECTORTYPE=${LOGCOLLECTORTYPE}" >> logging_vars.sh
                echo "export OCPVERSION=${OCPVERSION}" >> logging_vars.sh
                echo "export CLFCLEANUP=${CLFCLEANUP}" >> logging_vars.sh
                
                ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP} "rm -rf /root/ocp_ansible_validation"
                ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP} "git clone https://satwsin1:${GITHUBTOKEN}@github.ibm.com/redstack-power/ocp_ansible_validation.git"  

                scp -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa ${WORKSPACE}/deploy/ocp_cluster_logging_vars.yml root@${OCP_IP}:/root/ocp_ansible_validation/examples/
                scp -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa ${WORKSPACE}/logging_vars.sh root@${OCP_IP}:/root/ 
                scp -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa ${WORKSPACE}/deploy/cl_inventory root@${OCP_IP}:/root/ocp_ansible_validation/

                ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP} "hostname -s >> logging_vars.sh; chmod +x logging_vars.sh; source logging_vars.sh"
                ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP} "cd /root/ocp_ansible_validation/; BASTION_HOSTNAME=`hostname -s`; sed -i 's|BASTION_HOSTNAME|'"${BASTION_HOSTNAME}"'|g' cl_inventory" 
                ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP} "source logging_vars.sh; cd /root/ocp_ansible_validation/examples/; sed -i 's|{CHANNEL}|'"${CHANNEL}"'|g' ocp_cluster_logging_vars.yml;  sed -i 's|{ELASTICSEARCH_INDEX}|'"${ELASTICSEARCH_INDEX}"'|g' ocp_cluster_logging_vars.yml; sed -i 's|{CLUSTERLOGGING_INDEX}|'"${CLUSTERLOGGING_INDEX}"'|g' ocp_cluster_logging_vars.yml; sed -i 's|{CLUSTERLOGGING_INDEX}|'"${CLUSTERLOGGING_INDEX}"'|g' ocp_cluster_logging_vars.yml; "
                ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP} "source logging_vars.sh; cd /root/ocp_ansible_validation/examples/; sed -i 's|{OCPVERSION}|'"${OCPVERSION}"'|g' ocp_cluster_logging_vars.yml; sed -i 's|{LOGCOLLECTORTYPE}|'"${LOGCOLLECTORTYPE}"'|g' ocp_cluster_logging_vars.yml; sed -i 's|{CLFCLEANUP}|'"${CLFCLEANUP}"'|g' ocp_cluster_logging_vars.yml; "
                ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP} "cd /root/ocp_ansible_validation/; ansible-playbook  -i cl_inventory -e @examples/ocp_cluster_logging_vars.yml playbooks/main.yml >> /root/cluster_logging_output.txt"
                scp -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP}:/root/cluster_logging_output.txt ${WORKSPACE}/
            '''
        }
        catch (err) {
            echo 'Error ! Setup Vars failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}