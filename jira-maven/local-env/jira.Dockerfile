# syntax=docker/dockerfile:1
ARG JIRA_VERSION
FROM atlassian/jira-core:${JIRA_VERSION}
ARG FRONTEND_DEV_MODE

RUN test -n "$JIRA_VERSION"
RUN test -n "$FRONTEND_DEV_MODE"

# Install deps
RUN apt-get update
RUN apt-get install -y xmlstarlet

# Configure jira
ENV JVM_SUPPORT_RECOMMENDED_ARGS="-XX:+UseG1GC \
 -Xdebug \
 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 \
 -Dfrontend.devMode=${FRONTEND_DEV_MODE-false} \
 -Dupm.plugin.upload.enabled=true"

# Disable default log and redirect logfile content to STDOUT
ENV JIRA_LOG="/opt/atlassian/jira/atlassian-jira/WEB-INF/classes/log4j2.xml"
COPY jira-log4j2.xml $JIRA_LOG

# Ports
EXPOSE 8080
EXPOSE 8091
EXPOSE 5005
