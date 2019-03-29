<?php

require_once("./lib/config_paytm.php");
require_once("./lib/encdec_paytm.php");

if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST') != 0){
    throw new Exception('Request method must be POST');
}

$content = trim(file_get_contents("php://input"));

$decodedData = json_decode($content, true);

        if(!empty($decodedData)){

            $paramList = array();

            $paramList["MID"] = $decodedData["MID"];
            $paramList["ORDER_ID"] = $decodedData["ORDER_ID"];
            $paramList["CUST_ID"] = $decodedData["CUST_ID"];
            $paramList["INDUSTRY_TYPE_ID"] = $decodedData["INDUSTRY_TYPE_ID"];
            $paramList["CHANNEL_ID"] = $decodedData["CHANNEL_ID"];
            $paramList["TXN_AMOUNT"] = $decodedData["TXN_AMOUNT"];
            $paramList["WEBSITE"] = $decodedData["WEBSITE"];
            $paramList["CALLBACK_URL"] = $decodedData["CALLBACK_URL"];
          //  $paramList["EMAIL"] = 'abc@gmail.com'
            $paramList["MOBILE_NO"] = $decodedData["MOBILE_NO"];

            $checkSum = getChecksumFromArray($paramList,PAYTM_MERCHANT_KEY);

            if(!empty($checkSum)){
              echo json_encode(array("CHECKSUMHASH"=>$checkSum,"ORDER_ID"=>$decodedData["ORDER_ID"],"STATUS"=>"1"));
            }else{
              echo json_encode(array("CHECKSUMHASH"=>$checkSum,"ORDER_ID"=>$decodedData["ORDER_ID"],"STATUS"=>"0"));
            }

        }
        else{
            echo "Something wrong";
        }

    if(!is_array($decodedData)){
      throw new Exception('Received content contains invalid JSON format');
    }

?>
