<?php
error_reporting(E_ALL^E_NOTICE);
include "koneksi.php"; 
// Path to move uploaded files
$target_path = "../uploads/bukti/";
 
// array for final json respone
$response = array();
 
// getting server ip address
$server_ip = gethostbyname(gethostname());
 
// final file url that is being uploaded
$file_upload_url = "http://mayerscartensz.esy.es/uploads/bukti/";

date_default_timezone_set("Asia/Jakarta");
$date = date("Y-m-d");
$via=$_POST['via'];
$jumlah=$_POST['jumlah'];
$token=$_POST['api_key'];
$keterangan=$_POST['ket'];

$response['via'] = $via;
$response['jumlah'] = $jumlah;
$response['token'] = $token;
$response['keterangan'] = $keterangan;

$query=mysql_query("SELECT id_user from user where token='".$token."'");
$data=mysql_fetch_array($query);
$response['id'] = $data['id_user'];
if (isset($_FILES['image']['name'])) {
	$filename= $token.str_replace("-","",$date).str_replace(":","",$time).basename($_FILES['image']['name']);
	$target_path = $target_path . $filename;
    try {
        // Throws exception incase file is not being moved
        if (!move_uploaded_file($_FILES['image']['tmp_name'], $target_path)) {
            // make error flag true
            $response['error'] = true;
            $response['message'] = 'Could not move the file!';
        }
		else{
			// File successfully uploaded
			$response['message'] = 'File uploaded successfully!';
			$response['error'] = false;
			$file_url=$file_upload_url . $filename;
			$response['file_path'] = $file_url;
		}
    } catch (Exception $e) {
        // Exception occurred. Make error flag true
        $response['error'] = true;
        $response['message'] = $e->getMessage();
    }
}
$query=mysql_query("UPDATE orders SET status='3', progres='Menunggu konfirmasi pembayaran oleh Admin' WHERE id_user='".$data['id_user']."' AND status='2'");
$query=mysql_query("INSERT INTO transaksi (id_user,via,jumlah,ket,tgl_transaksi,bukti) VALUES ('".$data['id_user']."','".$via."','".$jumlah."','".$keterangan."','".$date."','".$file_url."')");
// Echo final json response to client
if($query){
}else{
	$response['error']=true;
	$response['message'] = mysql_error();
}
echo json_encode($response);
?>