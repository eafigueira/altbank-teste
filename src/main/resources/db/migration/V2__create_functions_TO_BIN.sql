CREATE FUNCTION TO_BIN(uuid_str CHAR(36))
RETURNS BINARY(16)
DETERMINISTIC
RETURN UNHEX(REPLACE(uuid_str, '-', ''));