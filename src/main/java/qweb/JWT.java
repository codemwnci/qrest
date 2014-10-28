package qweb;

import spark.Request;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;

public class JWT {

	static String SECRET = "";
	
	public static long getId(Request req) {
		long uid = 0;
		try {
			JWSObject jwsObject = JWSObject.parse(req.headers("x-access-token"));
			JWSVerifier verifier = new MACVerifier(SECRET.getBytes());
			boolean verifiedSignature = jwsObject.verify(verifier);

			if (verifiedSignature) {
			    uid = ((Long)jwsObject.getPayload().toJSONObject().get("id")).longValue();
			}
		} catch (Exception e) { }
		
		return uid;
	}
	
	public static String createToken(long id) throws JOSEException {
		
		JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload("{\"username\": \"codemwnci\", \"id\": 1}"));
		JWSSigner signer = new MACSigner(SECRET.getBytes());
		jwsObject.sign(signer);
		return jwsObject.serialize();
	}
}
