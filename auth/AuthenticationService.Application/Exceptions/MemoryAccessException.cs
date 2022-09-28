[System.Serializable]
public class MemoryAccessException : System.Exception
{
    public MemoryAccessException(string message, System.Exception? inner = null) : base(message, inner) { }
    protected MemoryAccessException(
        System.Runtime.Serialization.SerializationInfo info,
        System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
}
