package ml.robiot.wrapperhivemqlibrary

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.BufferedInputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory

fun trustManagerFactory(
    caCrtFile: InputStream?
) : TrustManagerFactory {
    Security.addProvider(BouncyCastleProvider())

    val bis = BufferedInputStream(caCrtFile)
    val cf = CertificateFactory.getInstance("X.509")

    val caCert = bis.use {
        cf.generateCertificate(it) as X509Certificate
    }

    val caKs = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null, null)
        setCertificateEntry("cert-certificate", caCert)
    }

    return TrustManagerFactory
        .getInstance(TrustManagerFactory.getDefaultAlgorithm())
        .apply { init(caKs) }
}