package group.aelysium.rustyconnector.plugin.velocity.lib.kubernetes;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;

import java.io.IOException;

public class KubernetesService {
    private ApiClient client = Config.defaultClient();
    private CoreV1Api api = new CoreV1Api(client);

    public KubernetesService() throws IOException {
    }
}
