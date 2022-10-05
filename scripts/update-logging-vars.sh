#!/bin/bash

echo "export CHANNEL=${LOGGINGVERSION}" >  logging_vars.sh
echo "export CLUSTERLOGGING_INDEX=${CLUSTERLOGGING_INDEX}" >> logging_vars.sh
echo "export ELASTICSEARCH_INDEX=${ELASTICSEARCH_INDEX}" >> logging_vars.sh
echo "export LOKIOPERATOR_INDEX=${LOKIOPERATOR_INDEX}" >> logging_vars.sh
echo "export LOGCOLLECTORTYPE=${LOGCOLLECTORTYPE}" >> logging_vars.sh
echo "export OCPVERSION=${OCPVERSION}" >> logging_vars.sh
echo "export CLFCLEANUP=${CLFCLEANUP}" >> logging_vars.sh

chmod +x logging_vars.sh
source /root/logging_vars.sh

pushd ocp_ansible_validation

BASTION_HOSTNAME=`hostname -s`
echo $BASTION_HOSTNAME
sed -i 's|BASTION_HOSTNAME|"'$BASTION_HOSTNAME'"|g' cl_inventory

popd

pushd ocp_ansible_validation/examples

echo $CHANNEL
echo $ELASTICSEARCH_INDEX
sed -i 's|{CHANNEL}|$CHANNEL|g' ocp_cluster_logging_vars.yml
sed -i 's|{ELASTICSEARCH_INDEX}|$ELASTICSEARCH_INDEX|g' ocp_cluster_logging_vars.yml
sed -i 's|{CLUSTERLOGGING_INDEX}|"'$CLUSTERLOGGING_INDEX'"|g' ocp_cluster_logging_vars.yml
sed -i 's|{CLUSTERLOGGING_INDEX}|"'$CLUSTERLOGGING_INDEX'"|g' ocp_cluster_logging_vars.yml
sed -i 's|{LOGCOLLECTORTYPE}|"'$LOGCOLLECTORTYPE'"|g' ocp_cluster_logging_vars.yml
sed -i 's|{CLFCLEANUP}|"'$CLFCLEANUP'"|g' ocp_cluster_logging_vars.yml

pushd ../
ansible-playbook -i cl_inventory -e @examples/ocp_cluster_logging_vars.yml playbooks/main.yml | tee /root/cluster_logging_output.txt
