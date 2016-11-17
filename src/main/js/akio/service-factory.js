/**
 * Created by ftronche@akio.com on 16/11/16.
 */
importScripts("lib/vertx-service-discovery-js/service_discovery");
var namedNodeMap;
var ServiceFactory = (function () {
    function ServiceFactory() {
    }
    return ServiceFactory;
}());
var ServiceMetadatas = (function () {
    function ServiceMetadatas(name, host, port, someMetadatas) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.someMetadatas = someMetadatas;
    }
    ServiceMetadatas.prototype.setName = function (name) {
        this.name = name;
    };
    return ServiceMetadatas;
}());
