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
  <img src="https://user-images.githubusercontent.com/123496162/219880393-67419661-b7ba-47f6-8d05-cecff12718a1.jpg"/>
  <img src="https://user-images.githubusercontent.com/123496162/219880398-c86c1716-9001-4dd1-bd0a-fd875628c406.jpg"/>
  <img src="https://user-images.githubusercontent.com/123496162/219880407-e086c447-c750-428b-b056-ba83c50395d1.jpg"/>
  <img src="https://user-images.githubusercontent.com/123496162/219880444-63d35fa4-6987-4a86-afe9-0f5f6a127129.jpg"/>
</p>

<p align="center">
<img src="https://user-images.githubusercontent.com/123496162/219880484-c3e903c0-7d7f-47bd-98fa-686dcec5a987.png"/>
<img src="https://user-images.githubusercontent.com/123496162/219880605-ab241bfe-8c7a-4262-b8db-3daf4e40df9f.png"/>
<img src="https://user-images.githubusercontent.com/123496162/219880500-a85b5b8d-565e-4815-b700-ed59f753ab6b.png"/>
</p>  
