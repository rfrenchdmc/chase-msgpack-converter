FROM bitnami/kafka

COPY plugins /opt/plugins
COPY mm2.properties /opt/mm2.properties

ENTRYPOINT ["/opt/bitnami/kafka/bin/connect-mirror-maker.sh"]
CMD ["/opt/mm2.properties"]
