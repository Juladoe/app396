cordova.exec(
                function(array) {
                   alert(array);
                },
                function(error) {
                    alert(error);
                },
                 "UtilPlugin",
                 "platformString",
                 [""]
            );