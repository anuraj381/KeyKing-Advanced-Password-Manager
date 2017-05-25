<?php

include('your-sms-api/gateway.php');

if(isset($_GET['mobile']) && isset($_GET['msg'])){
	$msg = $_GET['msg'];
	$mobile = $_GET['mobile'];
	your-function-to-send-sms ($mobile , $msg);
	
	echo "Your data from php page is as follows : mobile is $mobile and message is $msg";
}else{
	echo "data is not set";
}

?>