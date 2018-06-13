
FROM 320083444745.dkr.ecr.us-west-2.amazonaws.com/official/ruby:2.5.0-alpine3.7

ARG NPM_USER
ARG NPM_PASS

RUN apk update
RUN apk add --update \
  bash curl git nodejs openssh python3 py-pip \
  && rm -rf /var/cache/apk/*
RUN pip install --upgrade pip
RUN gem install license_finder
RUN pip install awscli --upgrade
RUN npm i -g yarn babel-cli
RUN addgroup -g 1000 jenkins && adduser -S -u 1000 -s /bin/bash -G jenkins jenkins

WORKDIR /app
RUN chown -R jenkins:jenkins /app

USER jenkins
RUN npm set registry https://rival.jfrog.io/rival/api/npm/npm

# Copy and install node modules first so they get cached
COPY --chown=jenkins:jenkins ./package.json ./yarn.lock /app/
RUN curl -u$NPM_USER:$NPM_PASS https://rival.jfrog.io/rival/api/npm/auth >> ~/.npmrc && yarn install; rm ~/.npmrc

ADD --chown=jenkins:jenkins . /app
