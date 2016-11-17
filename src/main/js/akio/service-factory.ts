/**
 * Created by ftronche@akio.com on 16/11/16.
 */
importScripts("lib/vertx-service-discovery-js/service_discovery")
var namedNodeMap:NamedNodeMap;
class ServiceFactory {

    constructor() {

    }

}

class ServiceMetadatas {
    constructor(public name: String, public host: String, public port: number, public someMetadatas: ServiceMetadatas) {
    }

    setName(name: String) {
        this.name = name;
    }
}