# Overview

This topic describes how to prepare branches of youк repositories  to start working with scm4j.

# Migration

Let your product consists of three components:

- com.mycompany:product
- com.mycompany:server
- com.mycompany:client

To start with scm4j

- Create `mdeps` file for `product` component (`develop` branch), version part may be avoided at this step
- Create `version` file with appropriate version in all components `develop` branches. If `develop` branch of any component does not have `version` file scm4j fails.

Component versions can be ommited in `mdeps`, they will be automatically taken from components `version` files, so `mdeps` file can looks like:
```ini
com.mycompany:server
com.mycompany:client
```

**`#scm-ignore` usage**

If last commit contains #scm-ignore all changes will be ignored


