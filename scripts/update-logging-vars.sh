#!/bin/bash

export CHANNEL=${LoggingVersion} 
export CLUSTERLOGGING_INDEX=${ClusterLogging_index} 
export ELASTICSEARCH_INDEX=${Elasticsearch_index}
export LOKIOPERATOR_INDEX=${LokiOperator_index}
export LOGCOLLECTORTYPE=${LogCollectorType}
export OCPVERSION=${OCPVersion}
export CLFCLEANUP=${CLFCLEANUP}

pushd ocp_ansible_validation

BASTION_HOSTNAME=`hostname -s`
echo $BASTION_HOSTNAME
sed -i 's|BASTION_HOSTNAME|"'$BASTION_HOSTNAME'"|g' cl_inventory

popd

pushd ocp_ansible_validation/examples

echo $CHANNEL
echo ${LoggingVersion}
echo $ELASTICSEARCH_INDEX
sed -i 's|{CHANNEL}|'$CHANNEL'|g' ocp_cluster_logging_vars.yml
sed -i 's|{ELASTICSEARCH_INDEX}|'$ELASTICSEARCH_INDEX'|g' ocp_cluster_logging_vars.yml
sed -i 's|{CLUSTERLOGGING_INDEX}|'$CLUSTERLOGGING_INDEX'|g' ocp_cluster_logging_vars.yml
sed -i 's|{CLUSTERLOGGING_INDEX}|'$CLUSTERLOGGING_INDEX'|g' ocp_cluster_logging_vars.yml
sed -i 's|{LOGCOLLECTORTYPE}|'$LOGCOLLECTORTYPE'|g' ocp_cluster_logging_vars.yml
sed -i 's|{CLFCLEANUP}|'$CLFCLEANUP'|g' ocp_cluster_logging_vars.yml

pushd ../
ansible-playbook -i cl_inventory -e @examples/ocp_cluster_logging_vars.yml playbooks/main.yml | tee /root/cluster_logging_output.txt
