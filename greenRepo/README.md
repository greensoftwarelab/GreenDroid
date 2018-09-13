# greenSourceBackend

##SETUP

###ctivate virtual env
####source greenRepoVirtualEnv/bin/activate

###create  and setup database
####TODO
####psql -h localhost -U postgres -d watergenius

####CREATE DATABASE repoApp;

###install required packages

####pip install -r requirements.txt --upgrade

## START SERVER
####python manage.py makemigrations
####python manage.py migrate
####python manage.py runserver


## NOTES
### Metrics coeficient refers the factor relative to the IS (International System) units ( 1 second -> coeficient 1, 13 ms -> coeficient = 0.001)


```
## API

- AndroidProject
  - [GET /projects/]
  - [POST /projects/]

- Application
  - [GET /apps/](#get-apps)
  - [POST /apps/](#post-apps)
  - [GET /apps/permissions/](#get-permissions)
  - [POST /apps/permissions/](#post-permissions)
  - [GET /apps/<app_id>/](#get-app)
  - [POST /apps/<app_id>/](#post-app)
  - [GET /apps/<app_id>/tests/](#app-tests)
  - [POST /apps/<app_id>/tests/](#app-tests)
  - [GET /apps/<app_id>/results/](#app-results)
  - [POST /apps/<app_id>/results/](#app-results)
  - [GET /apps/<app_id>/classes/](#app-classes)
  - [POST /apps/<app_id>/classes/](#app-classes)
  - [GET /apps/<app_id>/methods/](#app-methods)
  - [POST /apps/<app_id>/methods/](#app-methods)
  - [GET /apps/<app_id>/methods/<method_id>/](#app-method)
  - [POST /apps/<app_id>/methods/<method_id>/](#app-method)
  - [GET /apps/metrics/](#app-metrics)
  - [POST /apps/metrics/](#app-metrics)

- Class
  - [GET /apps/<app_id>/classes/](#get-class)
  - [POST /apps/<app_id>/classes/](#post-class)
  - [GET /classes/](#get-classes)
  - [POST /classes/](#post-classes)
  - [GET /classes/metrics/](#class-metrics)
  - [POST /classes/metrics/](#class-metrics)

- Method
  - [GET /apps/<app_id>/methods/](#app-methods)
  - [POST /apps/<app_id>/methods/](#app-methods)
  - [GET /methods/](#methods)
  - [POST /methods/](#methods)
  - [GET /methods/metrics/](#methods-metrics)
  - [POST /methods/metrics/](#methods-metrics)

- Test 
  - [GET /tests/](#app-tests)
  - [POST /tests/](#app-tests)
  - [GET /tests/<test_id>/results/](#test-results)
  - [POST /tests/<test_id>/results/](#test-results)
  - [GET /tests/metrics/](#app-tests)
  - [POST /tests/metrics/](#app-tests)

- Device
  - [GET /devices/](#devices)
  - [POST /devices/](#devices)

- Result
  - [GET /results/](#results)
  - [POST /results/](#results)

---




```

### POST /populate/
Povoa a BD com metricas, tools e dados relevantes.

### POST /populateTest/
Povoa a BD com dados dummy para fins de testing.

### GET /projects/
#### Response (CODE HTTP200)
```json
[
	{
	      "project_id": "2bb46be6-f071-413d-9221-c090a8f0cb29",
	      "project_build_tool": "gradle",
	      "project_desc": ""
    },
    {
	      "project_id": "xxx2bb46be6-f071-413d-9221-c090a8f0cb29",
	      "project_build_tool": "gradle",
	      "project_desc": ""
    }
]
```
#### Query Parameters
- *project_build_tool*: filter by ``project_build_tool``
- *project_id*: filter by  ``project_id``

### POST /projects/
#### Body
```json
[
	{
	      "project_id": "2bb46be6-f071-413d-9221-c090a8f0cb29",
	      "project_build_tool": "gradle",
	      "project_desc": ""
    }
]
```
### GET /projects/<project_id>/
#### Response (CODE HTTP200)
```json
[
	{
	      "project_id": "2bb46be6-f071-413d-9221-c090a8f0cb29",
	      "project_build_tool": "gradle",
	      "project_desc": "",
	      "project_apps": [     
					        {
					            "app_id": "dummy_app",
					            "app_location": "/Users/dummyUser/apps/dummyApp",
					            "app_description": "dumb",
					            "app_version": 1.1,
					            "app_flavor": "demo",
					            "app_build_type": "",
					            "app_project": "xxxx-dummy-xxxx-project-test"
					        }
    					  ]
    }
]
```

### GET /apps/
#### Query Parameters
- *app_id*: filter by  ``app_id``
- *app_description*: filter if contains``app_description``
- *app_flavor*: filter by   ``app_flavor`` (demo | full)
- *app_build_type*: filter by ``app_build_type`` (debug|release)
- *app_project*: filter by   ``app_project`` (demo | full)

#### Response (CODE HTTP200)
```json
[
  {
    "app_id": "dummy_app",
    "app_location": "/Users/dummyUser/apps/dummyApp",
    "app_description": "dumb",
    "app_version": 1.1,
    "app_flavor": "demo",
    "app_build_type": "",
    "app_project": "xxxx-dummy-xxxx-project-test"
    }
]
```

### POST /apps/
#### Body
```json
[
  {
    "app_id": "dummy_app",
    "app_location": "/Users/dummyUser/apps/dummyApp",
    "app_description": "dumb",
    "app_version": 1.1,
    "app_flavor": "demo",
    "app_build_type": "",
    "app_project": "xxxx-dummy-xxxx-project-test"
    
  }
]
```

#### Response (CODE HTTP200)
```json
[
  {
    "app_id": "dummy_app",
    "app_location": "/Users/dummyUser/apps/dummyApp",
    "app_description": "dumb",
    "app_version": 1.1,
    "app_flavor": "demo",
    "app_build_type": "",
    "app_project": "xxxx-dummy-xxxx-project-test"
    }

]
```

### GET /apps/permissions/
#### Query Parameters
- *app_id*: filter by  ``app_id``
- *permission*: filter by   ``android.permission`` ( internet | write_external_storage | ...)

#### Response (CODE HTTP200)
```json
[
    {
        "application": "dummy_app",
        "permission": "internet"
    }
]
```


### GET /apps/metrics/
#### Query Parameters
- *app_name*: filter by  ``app_name`` 
- *app_metric*: filter by  ``app_metric`` 
- *app_metric_value*: filter by   ``app_metric_value``
- *app_metric_value_gte*: filter by  ``app_metric_value_gte >= value`` 
- *app_metric_value_lte*: filter by   ``app_metric_value_lte <= value``

#### Response (CODE HTTP200)
```json
[
   {
        "app_id": "dummy_app",
        "app_location": "/Users/dummyUser/apps/dummyApp",
        "app_description": "dumb",
        "app_version": 1.1,
        "app_flavor": "demo",
        "app_build_type": "",
        "app_project": "xxxx-dummy-xxxx-project-test",
        "app_metrics": [
            {
                "am_app": "dummy_app",
                "am_metric": "totalenergy",
                "am_value": 131,
                "am_value_text": "",
                "am_coeficient": 1,
                "am_timestamp": "2018-09-12T17:27:44.512776Z"
            }
        ]
    }
]
```

### POST /apps/metrics
#### Body
```json
[
   {
        "app_id": "dummy_app",
        "app_location": "/Users/dummyUser/apps/dummyApp",
        "app_description": "dumb",
        "app_version": 1.1,
        "app_flavor": "demo",
        "app_build_type": "",
        "app_project": "xxxx-dummy-xxxx-project-test",
        "app_metrics": [
            {
                "am_app": "dummy_app",
                "am_metric": "totalenergy",
                "am_value": 131,
                "am_value_text": "",
                "am_coeficient": 1,
                "am_timestamp": "2018-09-12T17:27:44.512776Z"
            }
        ]
    }
]
```

#### Response (CODE HTTP200)
```json
[
  { {
        "app_id": "dummy_app",
        "app_location": "/Users/dummyUser/apps/dummyApp",
        "app_description": "dumb",
        "app_version": 1.1,
        "app_flavor": "demo",
        "app_build_type": "",
        "app_project": "xxxx-dummy-xxxx-project-test",
        "app_metrics": [
            {
                "am_app": "dummy_app",
                "am_metric": "totalenergy",
                "am_value": 131,
                "am_value_text": "",
                "am_coeficient": 1,
                "am_timestamp": "2018-09-12T17:27:44.512776Z"
            }
        ]
    }

]
```






### GET /apps/<app_id>/

#### Response
```json
[
  {
    "app_id": "dummy_app",
    "app_location": "/Users/dummyUser/apps/dummyApp",
    "app_description": "dumb",
    "app_version": 1.1,
    "app_flavor": "demo",
    "app_build_type": "",
    "app_project": "xxxx-dummy-xxxx-project-test"
    }
]
```
## GET /apps/<app_id>/classes/
- *class_package*: filter by  ``class_package``
- *class_name*: filter by   ``class_name``
- *class_non_acc_mod*: filter by  ``class_non_acc_mod``
- *class_id*: filter by   ``class_id``
- *class_is_interface*: filter by   ``class_is_interface``
- *class_implemented_ifaces*:(interface implemented )filter by   ``class_implemented_ifaces``
- *class_superclass*: filter by   ``class_superclass``

#### Response (CODE HTTP200)
```json
[
    {
        "class_id": "com.dummy.dummyapp.DummyActivity",
        "class_name": "DummyActivity",
        "class_package": "com.dummy.dummyapp",
        "class_non_acc_mod": "abstract#",
        "class_app": "dummy_app",
        "class_acc_modifier": "public",
        "class_superclass": "Activity",
        "class_is_interface": false,
        "class_implemented_ifaces": ""
    }
]
```
### POST /apps/<app_id>/classes/
#### Body
```json
[
    {
        "class_id": "com.dummy.dummyapp.DummyActivity",
        "class_name": "DummyActivity",
        "class_package": "com.dummy.dummyapp",
        "class_non_acc_mod": "abstract#",
        "class_app": "dummy_app",
        "class_acc_modifier": "public",
        "class_superclass": "Activity",
        "class_is_interface": false,
        "class_implemented_ifaces": ""
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
    {
        "class_id": "com.dummy.dummyapp.DummyActivity",
        "class_name": "DummyActivity",
        "class_package": "com.dummy.dummyapp",
        "class_non_acc_mod": "abstract#",
        "class_app": "dummy_app",
        "class_acc_modifier": "public",
        "class_superclass": "Activity",
        "class_is_interface": false,
        "class_implemented_ifaces": ""
    }
]
```

## GET /apps/<app_id>/methods/
- *method_id*: filter by  ``method_id`` format ( package + . + classid + . + name(arg1#arg2#))
- *method_name*: filter by   ``method_name``
- *method_acc_modifier*: filter by  ``method_acc_modifier``
- *method_non_acc_mod*: filter by   ``method_non_acc_mod``


#### Response (CODE HTTP200)
```json
[
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod2",
        "method_name": "dummyMethod2",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_acc_modifier": "private",
        "method_non_acc_mod": "static#synchronized#"
    }
]
```
### POST /apps/<app_id>/methods/
#### Body
```json
[
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod(#int#String#),
        "method_name": "dummyMethod2",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_acc_modifier": "private",
        "method_non_acc_mod": "static#synchronized"
    },
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod()",
        "method_name": "dummyMethod2",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_acc_modifier": "public",
        "method_non_acc_mod": "static"
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod(#int#String#),
        "method_name": "dummyMethod2",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_acc_modifier": "private",
        "method_non_acc_mod": "static#synchronized"
    },
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod()",
        "method_name": "dummyMethod2",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_acc_modifier": "public",
        "method_non_acc_mod": "static"
    }
]
```

## GET /apps/<app_id>/methods/<method_name>/
- *method_id*: filter by  ``method_id`` format ( package + . + classid + . + name(arg1#arg2#) )


#### Response (CODE HTTP200)
```json
[
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod2",
        "method_name": "dummyMethod2",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_acc_modifier": "private",
        "method_non_acc_mod": "static#synchronized#"
    }
]
```


## GET /apps/<app_id>/tests/
- *test_tool*: filter by  ``test_tool`` 
- *test_orientation*: filter by   ``test_orientation``


#### Response (CODE HTTP200)
```json
[
    {
        "id": 1,
        "test_application": "dummy_app",
        "test_tool": "monkey",
        "test_orientation": "testoriented"
    }
]
```

## GET /apps/<app_id>/tests/results/
- *test_tool*: filter by  ``test_tool`` 
- *test_orientation*: filter by   ``test_orientation``
- *test_seed*: filter by  ``test_seed`` 
- *test_device*: filter by   ``device_serial_number``
- *test_profiler*: filter by   ``test_profiler``


#### Response (CODE HTTP200)
```json
[
    {
        "test_results_id": 1,
        "test_results_timestamp": "2018-09-11T13:46:59.115448Z",
        "test_results_seed": 1893,
        "test_results_description": "dd",
        "test_results_test": 1,
        "test_results_profiler": "trepn",
        "test_results_device": "X2222",
        "test_metrics": [
            {
                "test_results": 1,
                "metric": "wifistate",
                "value": 1,
                "value_text": "used",
                "coeficient": 1
            }
        ],
        "test_state_init": {
            "device_state_id": 1,
            "device_state_mem": 100,
            "device_state_cpu_free": 90,
            "device_state_nr_processes_running": 10,
            "device_state_api_level": 25,
            "device_state_android_version": 7.1
        },
        "test_state_end": {
            "device_state_id": 2,
            "device_state_mem": 960,
            "device_state_cpu_free": 30,
            "device_state_nr_processes_running": 11,
            "device_state_api_level": 22,
            "device_state_android_version": 6
        }
    }
]
```

## GET /classes/
- *class_package*: filter by  ``class_package``
- *class_name*: filter by   ``class_name``
- *class_non_acc_mod*: filter by  ``class_non_acc_mod``
- *class_id*: filter by   ``class_id``
- *class_is_interface*: filter by   ``class_is_interface``
- *class_implemented_ifaces*:(interface implemented )filter by   ``class_implemented_ifaces``
- *class_superclass*: filter by   ``class_superclass``


#### Response (CODE HTTP200)
```json
[
   {
        "class_id": "com.dummy.dummyapp.DummyActivity",
        "class_name": "DummyActivity",
        "class_package": "com.dummy.dummyapp",
        "class_non_acc_mod": "",
        "class_app": "dummy_app",
        "class_acc_modifier": "public",
        "class_superclass": "Activity",
        "class_is_interface": false,
        "class_implemented_ifaces": null
    }
]
```

### POST /classes/
#### Body
```json
[
   {
        "class_id": "com.dummy.dummyapp.DummyActivity",
        "class_name": "DummyActivity",
        "class_package": "com.dummy.dummyapp",
        "class_non_acc_mod": "",
        "class_app": "dummy_app",
        "class_acc_modifier": "public",
        "class_superclass": "Activity",
        "class_is_interface": false,
        "class_implemented_ifaces": null
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
    {
        "class_id": "com.dummy.dummyapp.DummyActivity",
        "class_name": "DummyActivity",
        "class_package": "com.dummy.dummyapp",
        "class_non_acc_mod": "",
        "class_app": "dummy_app",
        "class_acc_modifier": "public",
        "class_superclass": "Activity",
        "class_is_interface": false,
        "class_implemented_ifaces": null
    }
]
```



## GET /classes/metrics/
- *class_name*: filter by  ``class_name`` 
- *class_metric*: filter by  ``class_metric`` 
- *class_metric_value*: filter by   ``class_metric_value``
- *class_metric_value_gte*: filter by  ``class_metric_value >= value`` 
- *class_metric_value_lte*: filter by   ``class_metric_value <= value``


#### Response (CODE HTTP200)
```json
[
     {
        "class_id": "com.dummy.dummyapp.DummyActivity",
        "class_name": "DummyActivity",
        "class_package": "com.dummy.dummyapp",
        "class_non_acc_mod": "",
        "class_app": "dummy_app",
        "class_acc_modifier": "public",
        "class_superclass": "Activity",
        "class_is_interface": false,
        "class_implemented_ifaces": null,
        "class_metrics": [
            {
                "cm_class": "com.dummy.dummyapp.DummyActivity",
                "cm_timestamp": "2018-09-11T13:46:59.129746Z",
                "cm_metric": "totalenergy",
                "cm_value": 13,
                "cm_coeficient": 1,
                "cm_value_text": "totalmethods"
            }
        ]
    }
]
```

### POST /classes/metrics/
#### Body
```json
[
    {
        "class_id": "com.dummy.dummyapp.DummyActivity",
        "class_name": "DummyActivity",
        "class_package": "com.dummy.dummyapp",
        "class_non_acc_mod": "",
        "class_app": "dummy_app",
        "class_acc_modifier": "public",
        "class_superclass": "Activity",
        "class_is_interface": false,
        "class_implemented_ifaces": null,
        "class_metrics": [
            {
                "cm_class": "com.dummy.dummyapp.DummyActivity",
                "cm_timestamp": "2018-09-11T13:46:59.129746Z",
                "cm_metric": "totalenergy",
                "cm_value": 13,
                "cm_coeficient": 1,
                "cm_value_text": "totalmethods"
            }
        ]
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
    {
        "class_id": "com.dummy.dummyapp.DummyActivity",
        "class_name": "DummyActivity",
        "class_package": "com.dummy.dummyapp",
        "class_non_acc_mod": "",
        "class_app": "dummy_app",
        "class_acc_modifier": "public",
        "class_superclass": "Activity",
        "class_is_interface": false,
        "class_implemented_ifaces": null,
        "class_metrics": [
            {
                "cm_class": "com.dummy.dummyapp.DummyActivity",
                "cm_timestamp": "2018-09-11T13:46:59.129746Z",
                "cm_metric": "totalenergy",
                "cm_value": 13,
                "cm_coeficient": 1,
                "cm_value_text": "totalmethods"
            }
        ]
    }
]
```




## GET /devices/
- *device_serial_number*: filter by  ``device_serial_number`` 
- *device_brand*: filter by  ``device_brand`` 
- *device_model*: filter by   ``device_model``


#### Response (CODE HTTP200)
```json
[
    {
        "device_serial_number": "XFF2",
        "device_brand": "xiaomi",
        "device_model": "mi5"
    }
]
```

### POST /devices/
#### Body
```json
[
    {
        "device_serial_number": "XFF2",
        "device_brand": "xiaomi",
        "device_model": "mi5"
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
    {
        "device_serial_number": "XFF2",
        "device_brand": "xiaomi",
        "device_model": "mi5"
    }
]
```

## GET /tests/
- *test_tool*: filter by  ``test_tool`` 
- *test_orientation*: filter by   ``test_orientation``
- *test_application*: filter by   ``test_application``


#### Response (CODE HTTP200)
```json
[
    {
        "id": 1,
        "test_application": "dummy_app",
        "test_tool": "monkey",
        "test_orientation": "testoriented"
    }
]
```

### POST /tests/
#### Body
```json
[
    {
        "id": 1,
        "test_application": "dummy_app",
        "test_tool": "monkey",
        "test_orientation": "testoriented"
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
    {
        "id": 1,
        "test_application": "dummy_app",
        "test_tool": "monkey",
        "test_orientation": "testoriented"
    }
]
```

## GET /tests/<test_id>/results/
- *test_results_id*: filter by  ``test_results_id`` 
- *test_results_seed*: filter by  ``test_results_seed`` 
- *test_results_description*: contains description in    ``test_results_description``
- *test_results_profiler*: filter by  ``test_results_profiler`` 
- *test_results_device*: filter by  ``test_results_device`` 

#### Response (CODE HTTP200)
```json
[
    {
        "test_results_id": 1,
        "test_results_timestamp": "2018-09-11T13:46:59.115448Z",
        "test_results_seed": 1893,
        "test_results_description": "dd",
        "test_results_test": 1,
        "test_results_profiler": "trepn",
        "test_results_device": "X2222",
        "test_metrics": [
            {
                "test_results": 1,
                "metric": "wifistate",
                "value": 1,
                "value_text": "used",
                "coeficient": 1
            }
        ],
        "test_state_init": {
            "device_state_id": 1,
            "device_state_mem": 100,
            "device_state_cpu_free": 90,
            "device_state_nr_processes_running": 10,
            "device_state_api_level": 25,
            "device_state_android_version": 7.1
        },
        "test_state_end": {
            "device_state_id": 2,
            "device_state_mem": 960,
            "device_state_cpu_free": 30,
            "device_state_nr_processes_running": 11,
            "device_state_api_level": 22,
            "device_state_android_version": 6
        }
    }
]
```

### POST /tests/<test_id>/results/
#### Body
```json
[ 
	{
	    "test_results_id": 1,
	    "test_results_timestamp": "2018-09-11T13:46:59.115448Z",
	    "test_results_seed": 1893,
	    "test_results_description": "dd",
	    "test_results_test": 1,
	    "test_results_profiler": "trepn",
	    "test_results_device": "X2222"
   	}
]
```

#### Expected Response (CODE HTTP200)
```json
[
    {
	    "test_results_id": 1,
	    "test_results_timestamp": "2018-09-11T13:46:59.115448Z",
	    "test_results_seed": 1893,
	    "test_results_description": "dd",
	    "test_results_test": 1,
	    "test_results_profiler": "trepn",
	    "test_results_device": "X2222"
   	}
]
```

## GET /tests/metrics/
- *test_metric*: filter by  ``test_metric`` 
- *test_value*: filter by  ``test_value`` 
- *test_value_text*: filter by  ``test_value_text`` 

#### Response (CODE HTTP200)
```json
[
    {
        "test_results": 1,
        "metric": "wifistate",
        "value": 1,
        "value_text": "used",
        "coeficient": 1
    }
]
```

### POST /tests/metrics/
#### Body
```json
[ 
	{
        "test_results": 1,
        "metric": "wifistate",
        "value": 1,
        "value_text": "used",
        "coeficient": 1
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
    {
        "test_results": 1,
        "metric": "wifistate",
        "value": 1,
        "value_text": "used",
        "coeficient": 1
    }
]
```

### POST tests/results/
#### Body
```json
[ 
	{
        "test_results_id": 1,
        "test_results_timestamp": "2018-09-11T13:46:59.115448Z",
        "test_results_seed": 1893,
        "test_results_description": "dd",
        "test_results_test": 1,
        "test_results_profiler": "trepn",
        "test_results_device": "X2222",
        "test_results_device_begin_state": 1,
        "test_results_device_end_state": 2
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
    {
        "test_results_id": 4,
        "test_results_timestamp": "2018-09-11T13:46:59.115448Z",
        "test_results_seed": 1893,
        "test_results_description": "dd",
        "test_results_test": 1,
        "test_results_profiler": "trepn",
        "test_results_device": "X2222",
        "test_results_device_begin_state": 1,
        "test_results_device_end_state": 2
    }
]
```

## GET /results/
- *test_results_id*: filter by  ``test_results_id`` 
- *test_results_seed*: filter by  ``test_results_seed`` 
- *test_results_description*: contains description in    ``test_results_description``
- *test_results_profiler*: filter by  ``test_results_profiler`` 
- *test_results_device*: filter by  ``test_results_device`` 

#### Response (CODE HTTP200)
```json
[
    {
        "test_results_id": 1,
        "test_results_timestamp": "2018-09-11T13:46:59.115448Z",
        "test_results_seed": 1893,
        "test_results_description": "dd",
        "test_results_test": 1,
        "test_results_profiler": "trepn",
        "test_results_device": "X2222",
        "test_results_device_begin_state": 1,
        "test_results_device_end_state": 2
    },
    {
        "test_results_id": 2,
        "test_results_timestamp": "2018-10-11T13:46:59.115448Z",
        "test_results_seed": 18931,
        "test_results_description": "dd",
        "test_results_test": 2,
        "test_results_profiler": "trepn",
        "test_results_device": "X2222",
        "test_results_device_begin_state": 3,
        "test_results_device_end_state": 4
    }
]
```

### POST /results/
#### Body
```json
[ 
	 {
        "test_results_id": 2,
        "test_results_timestamp": "2018-10-11T13:46:59.115448Z",
        "test_results_seed": 18931,
        "test_results_description": "dd",
        "test_results_test": 2,
        "test_results_profiler": "trepn",
        "test_results_device": "X2222",
        "test_results_device_begin_state": 3,
        "test_results_device_end_state": 4
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
     {
        "test_results_id": 2,
        "test_results_timestamp": "2018-10-11T13:46:59.115448Z",
        "test_results_seed": 18931,
        "test_results_description": "dd",
        "test_results_test": 2,
        "test_results_profiler": "trepn",
        "test_results_device": "X2222",
        "test_results_device_begin_state": 3,
        "test_results_device_end_state": 4
    }
]
```

## GET /methods/
- *method_name*: filter by  ``method_name`` 
- *method_class*: filter by  ``method_class`` 
- *method_metric*: filter by  ``mm_metric`` 
- *method_metric_value*: filter by  ``mm_value`` 
- *method_metric_value_gte*: filter by  ``mm_value > value`` 
- *method_metric_value_lte*: filter by  ``mm_value < value`` 




#### Response (CODE HTTP200)
```json
[
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod",
        "method_name": "dummyMethod",
        "method_acc_modifier": "public",
        "method_non_acc_mod": "static",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_metrics": [
            {
                "mm_method": "com.dummy.dummyapp.DummyActivity.dummyMethod",
                "mm_metric": "totalenergy",
                "mm_value": 13,
                "mm_coeficient": 1,
                "mm_method_invoked": null
            }
        ]
    },
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod2",
        "method_name": "dummyMethod2",
        "method_acc_modifier": "private",
        "method_non_acc_mod": "",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_metrics": []
    }
]
```

### POST /methods/
#### Body
```json
[
     {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod",
        "method_name": "dummyMethod",
        "method_acc_modifier": "public",
        "method_non_acc_mod": "static",
        "method_class": "com.dummy.dummyapp.DummyActivity"
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
     {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod",
        "method_name": "dummyMethod",
        "method_acc_modifier": "public",
        "method_non_acc_mod": "static",
        "method_class": "com.dummy.dummyapp.DummyActivity"
    }
]
```

## GET /methods/
- *method_name*: filter by  ``method_name`` 
- *method_class*: filter by  ``method_class`` 
- *method_metric*: filter by  ``mm_metric`` 
- *method_metric_value*: filter by  ``mm_value`` 
- *method_metric_value_gte*: filter by  ``mm_value > value`` 
- *method_metric_value_lte*: filter by  ``mm_value < value`` 


#### Response (CODE HTTP200)
```json
[
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod",
        "method_name": "dummyMethod",
        "method_acc_modifier": "public",
        "method_non_acc_mod": "static",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_metrics": [
            {
                "mm_method": "com.dummy.dummyapp.DummyActivity.dummyMethod",
                "mm_metric": "totalenergy",
                "mm_value": 13,
                "mm_coeficient": 1,
                "mm_method_invoked": null
            }
        ]
    },
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod2",
        "method_name": "dummyMethod2",
        "method_acc_modifier": "private",
        "method_non_acc_mod": "",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_metrics": []
    }
]
```

### POST /methods/
#### Body
```json
[
     {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod",
        "method_name": "dummyMethod",
        "method_acc_modifier": "public",
        "method_non_acc_mod": "static",
        "method_class": "com.dummy.dummyapp.DummyActivity"
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
     {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod",
        "method_name": "dummyMethod",
        "method_acc_modifier": "public",
        "method_non_acc_mod": "static",
        "method_class": "com.dummy.dummyapp.DummyActivity"
    }
]
```

## GET /methods/metrics/
- *method_name*: filter by  ``method_name`` 
- *method_class*: filter by  ``method_class`` 
- *method_app*: filter by  ``app_id`` 


#### Response (CODE HTTP200)
```json
[
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod",
        "method_name": "dummyMethod",
        "method_acc_modifier": "public",
        "method_non_acc_mod": "static",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_metrics": [
            {
                "mm_method": "com.dummy.dummyapp.DummyActivity.dummyMethod",
                "mm_metric": "totalenergy",
                "mm_value": 13,
                "mm_coeficient": 1,
                "mm_method_invoked": null
            }
        ]
    },
    {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod2",
        "method_name": "dummyMethod2",
        "method_acc_modifier": "private",
        "method_non_acc_mod": "",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_metrics": []
    }
]
```

### POST /methods/metrics/
#### inserts methods and metrics in repo
#### Body
```json
[
     {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod2",
        "method_name": "dummyMethod2",
        "method_acc_modifier": "private",
        "method_non_acc_mod": "",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_metrics": []
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
     {
        "method_id": "com.dummy.dummyapp.DummyActivity.dummyMethod2",
        "method_name": "dummyMethod2",
        "method_acc_modifier": "private",
        "method_non_acc_mod": "",
        "method_class": "com.dummy.dummyapp.DummyActivity",
        "method_metrics": []
    }
]
```


### POST /methods/invoked/
#### inserts invokation of method by test in repo
#### Body
```json
[
     {
        "method": "com.dummy.dummyapp.DummyActivity.dummyMethod",
        "test_results": "1"
    }
]
```

#### Expected Response (CODE HTTP200)
```json
[
	{
        "method": "com.dummy.dummyapp.DummyActivity.dummyMethod",
        "test_results": "1"
    }
]
```


