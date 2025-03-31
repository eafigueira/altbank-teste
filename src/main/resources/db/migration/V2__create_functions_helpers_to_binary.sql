--- convert uuid to binary
CREATE FUNCTION TO_BIN(uuid_str CHAR(36))
RETURNS BINARY(16)
DETERMINISTIC
RETURN UNHEX(REPLACE(uuid_str, '-', ''));

--- convert binary to uuid
CREATE FUNCTION TO_UUID(bin_id BINARY(16))
RETURNS CHAR(36)
DETERMINISTIC
RETURN LOWER(CONCAT(
  SUBSTR(HEX(bin_id), 1, 8), '-',
  SUBSTR(HEX(bin_id), 9, 4), '-',
  SUBSTR(HEX(bin_id), 13, 4), '-',
  SUBSTR(HEX(bin_id), 17, 4), '-',
  SUBSTR(HEX(bin_id), 21)