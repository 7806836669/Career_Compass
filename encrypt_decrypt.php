<?php
// encrypt_decrypt.php

define('ENCRYPTION_KEY', 'your32characterlongencryptionkey!'); // 32 chars for AES-256
define('CIPHER_METHOD', 'AES-256-CBC');
define('IV', substr(hash('sha256', 'your_secret_iv'), 0, 16)); // generate 16-byte IV

function encrypt($plaintext) {
    return base64_encode(openssl_encrypt($plaintext, CIPHER_METHOD, ENCRYPTION_KEY, 0, IV));
}

function decrypt($ciphertext) {
    return openssl_decrypt(base64_decode($ciphertext), CIPHER_METHOD, ENCRYPTION_KEY, 0, IV);
}
?>
