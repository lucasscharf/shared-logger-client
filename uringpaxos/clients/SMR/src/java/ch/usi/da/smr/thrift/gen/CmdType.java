/**
 * Autogenerated by Thrift Compiler (0.14.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package ch.usi.da.smr.thrift.gen;


@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.14.1)", date = "2021-04-23")
public enum CmdType implements org.apache.thrift.TEnum {
  GET(0),
  GETRANGE(1),
  PUT(2),
  DELETE(3),
  RESPONSE(4);

  private final int value;

  private CmdType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  @org.apache.thrift.annotation.Nullable
  public static CmdType findByValue(int value) { 
    switch (value) {
      case 0:
        return GET;
      case 1:
        return GETRANGE;
      case 2:
        return PUT;
      case 3:
        return DELETE;
      case 4:
        return RESPONSE;
      default:
        return null;
    }
  }
}
