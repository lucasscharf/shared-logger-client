/**
 * Autogenerated by Thrift Compiler (0.14.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package ch.usi.da.dmap.thrift.gen;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.14.1)", date = "2021-04-23")
public class Command implements org.apache.thrift.TBase<Command, Command._Fields>, java.io.Serializable, Cloneable, Comparable<Command> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Command");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("type", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField KEY_FIELD_DESC = new org.apache.thrift.protocol.TField("key", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField VALUE_FIELD_DESC = new org.apache.thrift.protocol.TField("value", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField SNAPSHOT_FIELD_DESC = new org.apache.thrift.protocol.TField("snapshot", org.apache.thrift.protocol.TType.I64, (short)5);
  private static final org.apache.thrift.protocol.TField PARTITION_VERSION_FIELD_DESC = new org.apache.thrift.protocol.TField("partition_version", org.apache.thrift.protocol.TType.I64, (short)6);
  private static final org.apache.thrift.protocol.TField MAP_NUMBER_FIELD_DESC = new org.apache.thrift.protocol.TField("map_number", org.apache.thrift.protocol.TType.I32, (short)7);
  private static final org.apache.thrift.protocol.TField VALUE2_FIELD_DESC = new org.apache.thrift.protocol.TField("value2", org.apache.thrift.protocol.TType.STRING, (short)8);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CommandStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CommandTupleSchemeFactory();

  public long id; // required
  /**
   * 
   * @see CommandType
   */
  public @org.apache.thrift.annotation.Nullable CommandType type; // required
  public @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer key; // optional
  public @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer value; // optional
  public long snapshot; // optional
  public long partition_version; // required
  public int map_number; // optional
  public @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer value2; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "id"),
    /**
     * 
     * @see CommandType
     */
    TYPE((short)2, "type"),
    KEY((short)3, "key"),
    VALUE((short)4, "value"),
    SNAPSHOT((short)5, "snapshot"),
    PARTITION_VERSION((short)6, "partition_version"),
    MAP_NUMBER((short)7, "map_number"),
    VALUE2((short)8, "value2");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ID
          return ID;
        case 2: // TYPE
          return TYPE;
        case 3: // KEY
          return KEY;
        case 4: // VALUE
          return VALUE;
        case 5: // SNAPSHOT
          return SNAPSHOT;
        case 6: // PARTITION_VERSION
          return PARTITION_VERSION;
        case 7: // MAP_NUMBER
          return MAP_NUMBER;
        case 8: // VALUE2
          return VALUE2;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ID_ISSET_ID = 0;
  private static final int __SNAPSHOT_ISSET_ID = 1;
  private static final int __PARTITION_VERSION_ISSET_ID = 2;
  private static final int __MAP_NUMBER_ISSET_ID = 3;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.KEY,_Fields.VALUE,_Fields.SNAPSHOT,_Fields.MAP_NUMBER,_Fields.VALUE2};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.TYPE, new org.apache.thrift.meta_data.FieldMetaData("type", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, CommandType.class)));
    tmpMap.put(_Fields.KEY, new org.apache.thrift.meta_data.FieldMetaData("key", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.VALUE, new org.apache.thrift.meta_data.FieldMetaData("value", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.SNAPSHOT, new org.apache.thrift.meta_data.FieldMetaData("snapshot", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.PARTITION_VERSION, new org.apache.thrift.meta_data.FieldMetaData("partition_version", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.MAP_NUMBER, new org.apache.thrift.meta_data.FieldMetaData("map_number", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.VALUE2, new org.apache.thrift.meta_data.FieldMetaData("value2", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Command.class, metaDataMap);
  }

  public Command() {
  }

  public Command(
    long id,
    CommandType type,
    long partition_version)
  {
    this();
    this.id = id;
    setIdIsSet(true);
    this.type = type;
    this.partition_version = partition_version;
    setPartition_versionIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Command(Command other) {
    __isset_bitfield = other.__isset_bitfield;
    this.id = other.id;
    if (other.isSetType()) {
      this.type = other.type;
    }
    if (other.isSetKey()) {
      this.key = org.apache.thrift.TBaseHelper.copyBinary(other.key);
    }
    if (other.isSetValue()) {
      this.value = org.apache.thrift.TBaseHelper.copyBinary(other.value);
    }
    this.snapshot = other.snapshot;
    this.partition_version = other.partition_version;
    this.map_number = other.map_number;
    if (other.isSetValue2()) {
      this.value2 = org.apache.thrift.TBaseHelper.copyBinary(other.value2);
    }
  }

  public Command deepCopy() {
    return new Command(this);
  }

  @Override
  public void clear() {
    setIdIsSet(false);
    this.id = 0;
    this.type = null;
    this.key = null;
    this.value = null;
    setSnapshotIsSet(false);
    this.snapshot = 0;
    setPartition_versionIsSet(false);
    this.partition_version = 0;
    setMap_numberIsSet(false);
    this.map_number = 0;
    this.value2 = null;
  }

  public long getId() {
    return this.id;
  }

  public Command setId(long id) {
    this.id = id;
    setIdIsSet(true);
    return this;
  }

  public void unsetId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __ID_ISSET_ID);
  }

  /** Returns true if field id is set (has been assigned a value) and false otherwise */
  public boolean isSetId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __ID_ISSET_ID);
  }

  public void setIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __ID_ISSET_ID, value);
  }

  /**
   * 
   * @see CommandType
   */
  @org.apache.thrift.annotation.Nullable
  public CommandType getType() {
    return this.type;
  }

  /**
   * 
   * @see CommandType
   */
  public Command setType(@org.apache.thrift.annotation.Nullable CommandType type) {
    this.type = type;
    return this;
  }

  public void unsetType() {
    this.type = null;
  }

  /** Returns true if field type is set (has been assigned a value) and false otherwise */
  public boolean isSetType() {
    return this.type != null;
  }

  public void setTypeIsSet(boolean value) {
    if (!value) {
      this.type = null;
    }
  }

  public byte[] getKey() {
    setKey(org.apache.thrift.TBaseHelper.rightSize(key));
    return key == null ? null : key.array();
  }

  public java.nio.ByteBuffer bufferForKey() {
    return org.apache.thrift.TBaseHelper.copyBinary(key);
  }

  public Command setKey(byte[] key) {
    this.key = key == null ? (java.nio.ByteBuffer)null   : java.nio.ByteBuffer.wrap(key.clone());
    return this;
  }

  public Command setKey(@org.apache.thrift.annotation.Nullable java.nio.ByteBuffer key) {
    this.key = org.apache.thrift.TBaseHelper.copyBinary(key);
    return this;
  }

  public void unsetKey() {
    this.key = null;
  }

  /** Returns true if field key is set (has been assigned a value) and false otherwise */
  public boolean isSetKey() {
    return this.key != null;
  }

  public void setKeyIsSet(boolean value) {
    if (!value) {
      this.key = null;
    }
  }

  public byte[] getValue() {
    setValue(org.apache.thrift.TBaseHelper.rightSize(value));
    return value == null ? null : value.array();
  }

  public java.nio.ByteBuffer bufferForValue() {
    return org.apache.thrift.TBaseHelper.copyBinary(value);
  }

  public Command setValue(byte[] value) {
    this.value = value == null ? (java.nio.ByteBuffer)null   : java.nio.ByteBuffer.wrap(value.clone());
    return this;
  }

  public Command setValue(@org.apache.thrift.annotation.Nullable java.nio.ByteBuffer value) {
    this.value = org.apache.thrift.TBaseHelper.copyBinary(value);
    return this;
  }

  public void unsetValue() {
    this.value = null;
  }

  /** Returns true if field value is set (has been assigned a value) and false otherwise */
  public boolean isSetValue() {
    return this.value != null;
  }

  public void setValueIsSet(boolean value) {
    if (!value) {
      this.value = null;
    }
  }

  public long getSnapshot() {
    return this.snapshot;
  }

  public Command setSnapshot(long snapshot) {
    this.snapshot = snapshot;
    setSnapshotIsSet(true);
    return this;
  }

  public void unsetSnapshot() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __SNAPSHOT_ISSET_ID);
  }

  /** Returns true if field snapshot is set (has been assigned a value) and false otherwise */
  public boolean isSetSnapshot() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __SNAPSHOT_ISSET_ID);
  }

  public void setSnapshotIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __SNAPSHOT_ISSET_ID, value);
  }

  public long getPartition_version() {
    return this.partition_version;
  }

  public Command setPartition_version(long partition_version) {
    this.partition_version = partition_version;
    setPartition_versionIsSet(true);
    return this;
  }

  public void unsetPartition_version() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __PARTITION_VERSION_ISSET_ID);
  }

  /** Returns true if field partition_version is set (has been assigned a value) and false otherwise */
  public boolean isSetPartition_version() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __PARTITION_VERSION_ISSET_ID);
  }

  public void setPartition_versionIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __PARTITION_VERSION_ISSET_ID, value);
  }

  public int getMap_number() {
    return this.map_number;
  }

  public Command setMap_number(int map_number) {
    this.map_number = map_number;
    setMap_numberIsSet(true);
    return this;
  }

  public void unsetMap_number() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __MAP_NUMBER_ISSET_ID);
  }

  /** Returns true if field map_number is set (has been assigned a value) and false otherwise */
  public boolean isSetMap_number() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __MAP_NUMBER_ISSET_ID);
  }

  public void setMap_numberIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __MAP_NUMBER_ISSET_ID, value);
  }

  public byte[] getValue2() {
    setValue2(org.apache.thrift.TBaseHelper.rightSize(value2));
    return value2 == null ? null : value2.array();
  }

  public java.nio.ByteBuffer bufferForValue2() {
    return org.apache.thrift.TBaseHelper.copyBinary(value2);
  }

  public Command setValue2(byte[] value2) {
    this.value2 = value2 == null ? (java.nio.ByteBuffer)null   : java.nio.ByteBuffer.wrap(value2.clone());
    return this;
  }

  public Command setValue2(@org.apache.thrift.annotation.Nullable java.nio.ByteBuffer value2) {
    this.value2 = org.apache.thrift.TBaseHelper.copyBinary(value2);
    return this;
  }

  public void unsetValue2() {
    this.value2 = null;
  }

  /** Returns true if field value2 is set (has been assigned a value) and false otherwise */
  public boolean isSetValue2() {
    return this.value2 != null;
  }

  public void setValue2IsSet(boolean value) {
    if (!value) {
      this.value2 = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case ID:
      if (value == null) {
        unsetId();
      } else {
        setId((java.lang.Long)value);
      }
      break;

    case TYPE:
      if (value == null) {
        unsetType();
      } else {
        setType((CommandType)value);
      }
      break;

    case KEY:
      if (value == null) {
        unsetKey();
      } else {
        if (value instanceof byte[]) {
          setKey((byte[])value);
        } else {
          setKey((java.nio.ByteBuffer)value);
        }
      }
      break;

    case VALUE:
      if (value == null) {
        unsetValue();
      } else {
        if (value instanceof byte[]) {
          setValue((byte[])value);
        } else {
          setValue((java.nio.ByteBuffer)value);
        }
      }
      break;

    case SNAPSHOT:
      if (value == null) {
        unsetSnapshot();
      } else {
        setSnapshot((java.lang.Long)value);
      }
      break;

    case PARTITION_VERSION:
      if (value == null) {
        unsetPartition_version();
      } else {
        setPartition_version((java.lang.Long)value);
      }
      break;

    case MAP_NUMBER:
      if (value == null) {
        unsetMap_number();
      } else {
        setMap_number((java.lang.Integer)value);
      }
      break;

    case VALUE2:
      if (value == null) {
        unsetValue2();
      } else {
        if (value instanceof byte[]) {
          setValue2((byte[])value);
        } else {
          setValue2((java.nio.ByteBuffer)value);
        }
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return getId();

    case TYPE:
      return getType();

    case KEY:
      return getKey();

    case VALUE:
      return getValue();

    case SNAPSHOT:
      return getSnapshot();

    case PARTITION_VERSION:
      return getPartition_version();

    case MAP_NUMBER:
      return getMap_number();

    case VALUE2:
      return getValue2();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case ID:
      return isSetId();
    case TYPE:
      return isSetType();
    case KEY:
      return isSetKey();
    case VALUE:
      return isSetValue();
    case SNAPSHOT:
      return isSetSnapshot();
    case PARTITION_VERSION:
      return isSetPartition_version();
    case MAP_NUMBER:
      return isSetMap_number();
    case VALUE2:
      return isSetValue2();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof Command)
      return this.equals((Command)that);
    return false;
  }

  public boolean equals(Command that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_id = true;
    boolean that_present_id = true;
    if (this_present_id || that_present_id) {
      if (!(this_present_id && that_present_id))
        return false;
      if (this.id != that.id)
        return false;
    }

    boolean this_present_type = true && this.isSetType();
    boolean that_present_type = true && that.isSetType();
    if (this_present_type || that_present_type) {
      if (!(this_present_type && that_present_type))
        return false;
      if (!this.type.equals(that.type))
        return false;
    }

    boolean this_present_key = true && this.isSetKey();
    boolean that_present_key = true && that.isSetKey();
    if (this_present_key || that_present_key) {
      if (!(this_present_key && that_present_key))
        return false;
      if (!this.key.equals(that.key))
        return false;
    }

    boolean this_present_value = true && this.isSetValue();
    boolean that_present_value = true && that.isSetValue();
    if (this_present_value || that_present_value) {
      if (!(this_present_value && that_present_value))
        return false;
      if (!this.value.equals(that.value))
        return false;
    }

    boolean this_present_snapshot = true && this.isSetSnapshot();
    boolean that_present_snapshot = true && that.isSetSnapshot();
    if (this_present_snapshot || that_present_snapshot) {
      if (!(this_present_snapshot && that_present_snapshot))
        return false;
      if (this.snapshot != that.snapshot)
        return false;
    }

    boolean this_present_partition_version = true;
    boolean that_present_partition_version = true;
    if (this_present_partition_version || that_present_partition_version) {
      if (!(this_present_partition_version && that_present_partition_version))
        return false;
      if (this.partition_version != that.partition_version)
        return false;
    }

    boolean this_present_map_number = true && this.isSetMap_number();
    boolean that_present_map_number = true && that.isSetMap_number();
    if (this_present_map_number || that_present_map_number) {
      if (!(this_present_map_number && that_present_map_number))
        return false;
      if (this.map_number != that.map_number)
        return false;
    }

    boolean this_present_value2 = true && this.isSetValue2();
    boolean that_present_value2 = true && that.isSetValue2();
    if (this_present_value2 || that_present_value2) {
      if (!(this_present_value2 && that_present_value2))
        return false;
      if (!this.value2.equals(that.value2))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(id);

    hashCode = hashCode * 8191 + ((isSetType()) ? 131071 : 524287);
    if (isSetType())
      hashCode = hashCode * 8191 + type.getValue();

    hashCode = hashCode * 8191 + ((isSetKey()) ? 131071 : 524287);
    if (isSetKey())
      hashCode = hashCode * 8191 + key.hashCode();

    hashCode = hashCode * 8191 + ((isSetValue()) ? 131071 : 524287);
    if (isSetValue())
      hashCode = hashCode * 8191 + value.hashCode();

    hashCode = hashCode * 8191 + ((isSetSnapshot()) ? 131071 : 524287);
    if (isSetSnapshot())
      hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(snapshot);

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(partition_version);

    hashCode = hashCode * 8191 + ((isSetMap_number()) ? 131071 : 524287);
    if (isSetMap_number())
      hashCode = hashCode * 8191 + map_number;

    hashCode = hashCode * 8191 + ((isSetValue2()) ? 131071 : 524287);
    if (isSetValue2())
      hashCode = hashCode * 8191 + value2.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(Command other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetId(), other.isSetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id, other.id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetType(), other.isSetType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type, other.type);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetKey(), other.isSetKey());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetKey()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.key, other.key);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetValue(), other.isSetValue());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetValue()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.value, other.value);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetSnapshot(), other.isSetSnapshot());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSnapshot()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.snapshot, other.snapshot);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetPartition_version(), other.isSetPartition_version());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPartition_version()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.partition_version, other.partition_version);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetMap_number(), other.isSetMap_number());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMap_number()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.map_number, other.map_number);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetValue2(), other.isSetValue2());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetValue2()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.value2, other.value2);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Command(");
    boolean first = true;

    sb.append("id:");
    sb.append(this.id);
    first = false;
    if (!first) sb.append(", ");
    sb.append("type:");
    if (this.type == null) {
      sb.append("null");
    } else {
      sb.append(this.type);
    }
    first = false;
    if (isSetKey()) {
      if (!first) sb.append(", ");
      sb.append("key:");
      if (this.key == null) {
        sb.append("null");
      } else {
        org.apache.thrift.TBaseHelper.toString(this.key, sb);
      }
      first = false;
    }
    if (isSetValue()) {
      if (!first) sb.append(", ");
      sb.append("value:");
      if (this.value == null) {
        sb.append("null");
      } else {
        org.apache.thrift.TBaseHelper.toString(this.value, sb);
      }
      first = false;
    }
    if (isSetSnapshot()) {
      if (!first) sb.append(", ");
      sb.append("snapshot:");
      sb.append(this.snapshot);
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("partition_version:");
    sb.append(this.partition_version);
    first = false;
    if (isSetMap_number()) {
      if (!first) sb.append(", ");
      sb.append("map_number:");
      sb.append(this.map_number);
      first = false;
    }
    if (isSetValue2()) {
      if (!first) sb.append(", ");
      sb.append("value2:");
      if (this.value2 == null) {
        sb.append("null");
      } else {
        org.apache.thrift.TBaseHelper.toString(this.value2, sb);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class CommandStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CommandStandardScheme getScheme() {
      return new CommandStandardScheme();
    }
  }

  private static class CommandStandardScheme extends org.apache.thrift.scheme.StandardScheme<Command> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Command struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.id = iprot.readI64();
              struct.setIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.type = ch.usi.da.dmap.thrift.gen.CommandType.findByValue(iprot.readI32());
              struct.setTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // KEY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.key = iprot.readBinary();
              struct.setKeyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // VALUE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.value = iprot.readBinary();
              struct.setValueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // SNAPSHOT
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.snapshot = iprot.readI64();
              struct.setSnapshotIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // PARTITION_VERSION
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.partition_version = iprot.readI64();
              struct.setPartition_versionIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // MAP_NUMBER
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.map_number = iprot.readI32();
              struct.setMap_numberIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // VALUE2
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.value2 = iprot.readBinary();
              struct.setValue2IsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Command struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ID_FIELD_DESC);
      oprot.writeI64(struct.id);
      oprot.writeFieldEnd();
      if (struct.type != null) {
        oprot.writeFieldBegin(TYPE_FIELD_DESC);
        oprot.writeI32(struct.type.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.key != null) {
        if (struct.isSetKey()) {
          oprot.writeFieldBegin(KEY_FIELD_DESC);
          oprot.writeBinary(struct.key);
          oprot.writeFieldEnd();
        }
      }
      if (struct.value != null) {
        if (struct.isSetValue()) {
          oprot.writeFieldBegin(VALUE_FIELD_DESC);
          oprot.writeBinary(struct.value);
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetSnapshot()) {
        oprot.writeFieldBegin(SNAPSHOT_FIELD_DESC);
        oprot.writeI64(struct.snapshot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(PARTITION_VERSION_FIELD_DESC);
      oprot.writeI64(struct.partition_version);
      oprot.writeFieldEnd();
      if (struct.isSetMap_number()) {
        oprot.writeFieldBegin(MAP_NUMBER_FIELD_DESC);
        oprot.writeI32(struct.map_number);
        oprot.writeFieldEnd();
      }
      if (struct.value2 != null) {
        if (struct.isSetValue2()) {
          oprot.writeFieldBegin(VALUE2_FIELD_DESC);
          oprot.writeBinary(struct.value2);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CommandTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CommandTupleScheme getScheme() {
      return new CommandTupleScheme();
    }
  }

  private static class CommandTupleScheme extends org.apache.thrift.scheme.TupleScheme<Command> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Command struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetId()) {
        optionals.set(0);
      }
      if (struct.isSetType()) {
        optionals.set(1);
      }
      if (struct.isSetKey()) {
        optionals.set(2);
      }
      if (struct.isSetValue()) {
        optionals.set(3);
      }
      if (struct.isSetSnapshot()) {
        optionals.set(4);
      }
      if (struct.isSetPartition_version()) {
        optionals.set(5);
      }
      if (struct.isSetMap_number()) {
        optionals.set(6);
      }
      if (struct.isSetValue2()) {
        optionals.set(7);
      }
      oprot.writeBitSet(optionals, 8);
      if (struct.isSetId()) {
        oprot.writeI64(struct.id);
      }
      if (struct.isSetType()) {
        oprot.writeI32(struct.type.getValue());
      }
      if (struct.isSetKey()) {
        oprot.writeBinary(struct.key);
      }
      if (struct.isSetValue()) {
        oprot.writeBinary(struct.value);
      }
      if (struct.isSetSnapshot()) {
        oprot.writeI64(struct.snapshot);
      }
      if (struct.isSetPartition_version()) {
        oprot.writeI64(struct.partition_version);
      }
      if (struct.isSetMap_number()) {
        oprot.writeI32(struct.map_number);
      }
      if (struct.isSetValue2()) {
        oprot.writeBinary(struct.value2);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Command struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(8);
      if (incoming.get(0)) {
        struct.id = iprot.readI64();
        struct.setIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.type = ch.usi.da.dmap.thrift.gen.CommandType.findByValue(iprot.readI32());
        struct.setTypeIsSet(true);
      }
      if (incoming.get(2)) {
        struct.key = iprot.readBinary();
        struct.setKeyIsSet(true);
      }
      if (incoming.get(3)) {
        struct.value = iprot.readBinary();
        struct.setValueIsSet(true);
      }
      if (incoming.get(4)) {
        struct.snapshot = iprot.readI64();
        struct.setSnapshotIsSet(true);
      }
      if (incoming.get(5)) {
        struct.partition_version = iprot.readI64();
        struct.setPartition_versionIsSet(true);
      }
      if (incoming.get(6)) {
        struct.map_number = iprot.readI32();
        struct.setMap_numberIsSet(true);
      }
      if (incoming.get(7)) {
        struct.value2 = iprot.readBinary();
        struct.setValue2IsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

