package ae.dubaipolice.dpappstore.identityserviceconsumer;

import java.nio.charset.Charset;

import net.iharder.Base64;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import ae.dubaipolice.smartservices.dpappstore.utils.AppConstants;

public class RestServiceConsumer {
	private final static Logger log = Logger.getLogger(RestServiceConsumer.class);

	public RestResponseDTO consumerestService(String ConsumerURL, MultiValueMap<String, String> parametres) {

		RestResponseDTO responseDto = new RestResponseDTO();
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		MediaType mType = new MediaType(AppConstants.APPLICATION_MEDIATYPE, AppConstants.APPLICATION_FORM_URLENCODED, Charset.forName(AppConstants.CHAR_SET_UTF));
		headers.setContentType(mType);
		
		String authToken = Base64.encodeBytes((AppConstants.ESB_USERNAME + ":" + AppConstants.ESB_PASSWORD).getBytes());
		headers.add("Authorization", "Basic " + authToken);
		
		HttpEntity<MultiValueMap<String, String>> request = null;
		request = new HttpEntity<MultiValueMap<String, String>>(parametres, headers);
		try 
		{
		String response = restTemplate.postForObject(ConsumerURL, request,String.class);
//		Parsing String to JSon
			JSONParser parser = new JSONParser();
			JSONObject jsonMain = (JSONObject) parser.parse(response);
			JSONObject jsonSub = (JSONObject) parser.parse(jsonMain.get("map").toString());
			
			responseDto.setResponseCode((jsonSub.get("responseCode") != null) ? Integer.valueOf(jsonSub.get("responseCode").toString()) : 0);
			responseDto.setResponseMsg((jsonSub.get("responseMsg") != null) ? jsonSub.get("responseMsg").toString() : "");
		} catch (ParseException e) 
		{
			log.error(e);			
		}
		return responseDto;
	}

}
