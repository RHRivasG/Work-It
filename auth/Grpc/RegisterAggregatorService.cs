using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using Grpc.Core;
using Grpc.Net.Client;

namespace AuthenticationService.Grpc;

public static class RegisterAggregatorService {
    public static void UseServiceAggregator(this WebApplication app) {
        var config = app.Configuration;
        var serviceAggregatorHost = config["ServiceAggregator:Host"];
        var tlsCertsPath = config["ServiceAggregator:Certificate"];
        var tlsKeyPath = config["ServiceAggregator:Key"];

        app.Lifetime.ApplicationStarted.Register(async () => {
            using var client = await CreateServiceAggregatorClient(serviceAggregatorHost, tlsCertsPath, tlsKeyPath);
            await client.ServiceAggregatorClient.AddServiceAsync(new AddServiceMessage {
                Group = "auth",
                Capacity = 1 
            });
        });
        app.Lifetime.ApplicationStopped.Register(async () => {
            using var client = await CreateServiceAggregatorClient(serviceAggregatorHost, tlsCertsPath, tlsKeyPath);
            await client.ServiceAggregatorClient.UnsubscribeAsync(new UnsubscribeMessage());
        });
    }
    
    class ServiceAggregatorGrpcClient : IDisposable {
        public HttpClient HttpClient { private get; init; }
        public GrpcChannel GrpcChannel { private get; init; }
        public ServiceAggregator.ServiceAggregatorClient ServiceAggregatorClient { get; init; }

        public void Dispose()
        {
            HttpClient.Dispose();
            GrpcChannel.Dispose();
        }
    }
    
    static async Task<ServiceAggregatorGrpcClient> CreateServiceAggregatorClient(
        string serviceAggregatorHost, 
        string certificatePath, 
        string keyPath
    ) {
        var certificate = await File.ReadAllTextAsync(certificatePath);
        var key = await File.ReadAllTextAsync(keyPath);
        var cert = X509Certificate2.CreateFromPem(certificate, key);
        var httpHandler = new HttpClientHandler();
        httpHandler.ServerCertificateCustomValidationCallback = ValidateWithAuthority(cert);
        var client = new HttpClient(handler: httpHandler);
        var grpcChannel = GrpcChannel.ForAddress(serviceAggregatorHost, new GrpcChannelOptions() {
            HttpClient = client
        });
        return new ServiceAggregatorGrpcClient {
            GrpcChannel = grpcChannel,
            HttpClient = client,
            ServiceAggregatorClient = new ServiceAggregator.ServiceAggregatorClient(channel: grpcChannel)
        };
    }
    
    static Func<HttpRequestMessage, X509Certificate2?, X509Chain?, SslPolicyErrors, bool> ValidateWithAuthority(X509Certificate2 caCert) {
        return (HttpRequestMessage request, X509Certificate2? certificate2, X509Chain? chain, SslPolicyErrors policyErrors) => {
            if (certificate2 == null) return false;
            if (chain == null) return false;
            chain.ChainPolicy.TrustMode = X509ChainTrustMode.CustomRootTrust;
            chain.ChainPolicy.CustomTrustStore.Add(caCert);
            return chain.Build(certificate2);
        };
    }
}