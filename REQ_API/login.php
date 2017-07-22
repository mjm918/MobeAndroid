<?php
    require_once ('connection.php');

    if(isset($_POST['username']) && isset($_POST['password'])){

    	$result='';
    	$email = $_POST['username'];
        $password = $_POST['password'];

        $sql = "SELECT * FROM _user WHERE  email = '$email' AND pass = '$password'";
        $query = mysqli_query($con,$sql);
        $row = $query->fetch_assoc();
        $status = $row['status'];
        if(mysqli_num_rows($query)>0){
           if($status == "1"){
                $result = "-1";
           }else{
                $result = "1";
           }
        }else{
           $result = "0";
        }

       	echo $result;
    }else{
        echo "3";
    }
?>