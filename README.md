# Picauth
Used image (more user relatable token) for secret key generation in the process of 2-factor authentication setup.  
User need to provide an image during the 2FA setup phase which will then be passed to an algorithm for key generation.  
Created an algorithm for input images with some additional parameters to check if the image is strong enough for secure key generation.  
The generated key can then be used in a TOTP(Time-based OTP) generator app like Google Authenticator or Authy.  
  
This app is a demo app for the research based project topic "TOTP generation with image as a secret key for 2FA".  
  
step 1 : create an account & verify email  
step 2 : turn on two-factor authentication  
step 3 : follow the 2FA setup steps (providing an image, building key, sharing key to generator app & first setup verification).  
step 4 : logout and login again to test the 2FA, enter TOTP generated by your generator app into this app to get verified.  
  
pending work -> backup codes for every 2FA setup.


# App images

<p align="center">
  <img src="https://github.com/Shubh-Srivastava-5911/Picauth/assets/123496162/0ccca2a7-0637-45b6-a0eb-757e8eb8cf5f" width=300/>
  <img src="https://github.com/Shubh-Srivastava-5911/Picauth/assets/123496162/b5981415-c09a-4f98-bde6-d26c3df59bd2" width=300/>
  <img src="https://github.com/Shubh-Srivastava-5911/Picauth/assets/123496162/f986ced4-eee9-4b3e-97a0-84d86483a4bd" width=300/>
</p>

<p align="center">
<img src="https://github.com/Shubh-Srivastava-5911/Picauth/assets/123496162/24c194aa-6583-41bc-9d8c-e0ae911e627d" width=300/>
<img src="https://github.com/Shubh-Srivastava-5911/Picauth/assets/123496162/9b8f229a-f69f-4c95-8c9f-36c850db558b" width=300/>
<img src="https://github.com/Shubh-Srivastava-5911/Picauth/assets/123496162/ed75cd8a-65ab-4212-b641-89beabf31622" width=300/>
</p>  

<p align="center">
<img src="https://github.com/Shubh-Srivastava-5911/Picauth/assets/123496162/fc21b0dd-ae46-43c2-affb-38d69402f7b4" width=400/>
</p>
