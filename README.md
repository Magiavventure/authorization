# Magiavventure - Authorization
![Docker Image Version (latest semver)](https://img.shields.io/docker/v/magiavventure/app-magiavventure-authorization)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/Magiavventure/app-magiavventure-authorization/build.yml)

This service allows to login and save/update/find/delete user in Magiavventure App.

## Configuration

The properties exposed to configure this project are:

```properties
logging.level.it.magiavventure="string"                                                          # Logging level package magiavventure
magiavventure.lib.common.errors.service-errors-messages.{error-key}.code="string"                # The exception key error code
magiavventure.lib.common.errors.service-errors-messages.{error-key}.message="string"             # The exception key error message
magiavventure.lib.common.errors.service-errors-messages.{error-key}.description="string"         # The exception key error description
magiavventure.lib.common.errors.service-errors-messages.{error-key}.status=integer               # The exception key error status
```


## Error message map
The error message map is a basic system for return the specific message in the error response,
the configuration path is for branch **service-errors-messages**.
This branch setting a specific error message to **it.magiavventure.authorization.error.AuthorizationException**

## Running local
For run the service in local environment need to execute following actions

### Running service
Run the service with the following profile:
1. "local" for local environment configuration