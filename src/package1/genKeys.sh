#!/bin/bash
openssl genrsa -out RSApriv.pem 2048;
openssl rsa -pubout -in RSApriv.pem -out RSApub.pem;
openssl rsa -inform PEM -outform DER -pubin -pubout -in RSApub.pem -out RSApub.der;
openssl pkcs8 -topk8 -nocrypt -inform PEM -outform DER -in RSApriv.pem -out RSApriv.der;