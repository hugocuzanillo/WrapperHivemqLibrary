package ml.robiot.wrapperhivemqlibrary

import java.io.BufferedInputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory

fun keyManagerFactory(
    crtFile: InputStream?,
    keyFile: InputStream?,
    password: String
) : KeyManagerFactory {
    //val tag = "KeyManagerFactory"
    // load client certificate
    val bis = BufferedInputStream(crtFile)
    val cf = CertificateFactory.getInstance("X.509")
    val cert = bis.use {
        cf.generateCertificate(it) as X509Certificate
    }

    // load client private cert
    /*val pemParser = PEMParser(InputStreamReader(keyFile))
    val o = pemParser.readObject()
    val converter: JcaPEMKeyConverter = JcaPEMKeyConverter()
    val key: KeyPair = converter.getKeyPair(o as PEMKeyPair)*/
    val eK =  BufferedInputStream(keyFile)


    val ks = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null, null)
        setCertificateEntry("certificate", cert)
        eK.use {
            setKeyEntry(
                "private-cert",
                it.readBytes(),
                arrayOf<Certificate?>(cert)
            )
        }
        /*setKeyEntry(
            "private-cert",
            key.private,
            password.toCharArray(),
            arrayOf<Certificate?>(cert)
        )*/
    }

    return KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
        init(ks, password.toCharArray())
    }
}

