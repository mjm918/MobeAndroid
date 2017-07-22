<?php
    require_once ('connection.php');

    if($_SERVER['REQUEST_METHOD']=='POST'){
     $result = "";
     $fname = $_POST['fname'];
     $lname = $_POST['lname'];
     $status = $_POST['status'];
     $dob = $_POST['dob'];
     $email = $_POST['email'];
     $password = $_POST['password'];

     $sql = "SELECT * FROM _user WHERE email='$email'";

          $check = mysqli_query($con,$sql);

          if(mysqli_num_rows($check)>0){
             $result = "0";
          }else{
             $sql = "INSERT INTO _user (fname,lname,status,dob,email,pass) VALUES('$fname','$lname','$status','$dob','$email','$password')";
             if(mysqli_query($con,$sql)){
                 $result = '1';
             }else{
                 $result = '2';
             }
          }
     echo $result;
    }else{
        echo '3';
    }
?>