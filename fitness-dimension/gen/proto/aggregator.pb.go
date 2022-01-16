// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.27.1
// 	protoc        v3.19.2
// source: aggregator.proto

package proto

import (
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

type RequestServiceMessage struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Group string `protobuf:"bytes,1,opt,name=group,proto3" json:"group,omitempty"`
}

func (x *RequestServiceMessage) Reset() {
	*x = RequestServiceMessage{}
	if protoimpl.UnsafeEnabled {
		mi := &file_aggregator_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *RequestServiceMessage) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*RequestServiceMessage) ProtoMessage() {}

func (x *RequestServiceMessage) ProtoReflect() protoreflect.Message {
	mi := &file_aggregator_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use RequestServiceMessage.ProtoReflect.Descriptor instead.
func (*RequestServiceMessage) Descriptor() ([]byte, []int) {
	return file_aggregator_proto_rawDescGZIP(), []int{0}
}

func (x *RequestServiceMessage) GetGroup() string {
	if x != nil {
		return x.Group
	}
	return ""
}

type RequestServiceResponse struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Host string `protobuf:"bytes,1,opt,name=host,proto3" json:"host,omitempty"`
}

func (x *RequestServiceResponse) Reset() {
	*x = RequestServiceResponse{}
	if protoimpl.UnsafeEnabled {
		mi := &file_aggregator_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *RequestServiceResponse) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*RequestServiceResponse) ProtoMessage() {}

func (x *RequestServiceResponse) ProtoReflect() protoreflect.Message {
	mi := &file_aggregator_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use RequestServiceResponse.ProtoReflect.Descriptor instead.
func (*RequestServiceResponse) Descriptor() ([]byte, []int) {
	return file_aggregator_proto_rawDescGZIP(), []int{1}
}

func (x *RequestServiceResponse) GetHost() string {
	if x != nil {
		return x.Host
	}
	return ""
}

type AddServiceMessage struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Group    string `protobuf:"bytes,1,opt,name=group,proto3" json:"group,omitempty"`
	Capacity int32  `protobuf:"varint,3,opt,name=capacity,proto3" json:"capacity,omitempty"`
}

func (x *AddServiceMessage) Reset() {
	*x = AddServiceMessage{}
	if protoimpl.UnsafeEnabled {
		mi := &file_aggregator_proto_msgTypes[2]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *AddServiceMessage) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*AddServiceMessage) ProtoMessage() {}

func (x *AddServiceMessage) ProtoReflect() protoreflect.Message {
	mi := &file_aggregator_proto_msgTypes[2]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use AddServiceMessage.ProtoReflect.Descriptor instead.
func (*AddServiceMessage) Descriptor() ([]byte, []int) {
	return file_aggregator_proto_rawDescGZIP(), []int{2}
}

func (x *AddServiceMessage) GetGroup() string {
	if x != nil {
		return x.Group
	}
	return ""
}

func (x *AddServiceMessage) GetCapacity() int32 {
	if x != nil {
		return x.Capacity
	}
	return 0
}

type AddServiceResponse struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Status int32 `protobuf:"varint,3,opt,name=status,proto3" json:"status,omitempty"`
}

func (x *AddServiceResponse) Reset() {
	*x = AddServiceResponse{}
	if protoimpl.UnsafeEnabled {
		mi := &file_aggregator_proto_msgTypes[3]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *AddServiceResponse) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*AddServiceResponse) ProtoMessage() {}

func (x *AddServiceResponse) ProtoReflect() protoreflect.Message {
	mi := &file_aggregator_proto_msgTypes[3]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use AddServiceResponse.ProtoReflect.Descriptor instead.
func (*AddServiceResponse) Descriptor() ([]byte, []int) {
	return file_aggregator_proto_rawDescGZIP(), []int{3}
}

func (x *AddServiceResponse) GetStatus() int32 {
	if x != nil {
		return x.Status
	}
	return 0
}

type UnsubscribeMessage struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields
}

func (x *UnsubscribeMessage) Reset() {
	*x = UnsubscribeMessage{}
	if protoimpl.UnsafeEnabled {
		mi := &file_aggregator_proto_msgTypes[4]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *UnsubscribeMessage) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*UnsubscribeMessage) ProtoMessage() {}

func (x *UnsubscribeMessage) ProtoReflect() protoreflect.Message {
	mi := &file_aggregator_proto_msgTypes[4]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use UnsubscribeMessage.ProtoReflect.Descriptor instead.
func (*UnsubscribeMessage) Descriptor() ([]byte, []int) {
	return file_aggregator_proto_rawDescGZIP(), []int{4}
}

type UnsubscribeResponse struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Status int32 `protobuf:"varint,1,opt,name=status,proto3" json:"status,omitempty"`
}

func (x *UnsubscribeResponse) Reset() {
	*x = UnsubscribeResponse{}
	if protoimpl.UnsafeEnabled {
		mi := &file_aggregator_proto_msgTypes[5]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *UnsubscribeResponse) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*UnsubscribeResponse) ProtoMessage() {}

func (x *UnsubscribeResponse) ProtoReflect() protoreflect.Message {
	mi := &file_aggregator_proto_msgTypes[5]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use UnsubscribeResponse.ProtoReflect.Descriptor instead.
func (*UnsubscribeResponse) Descriptor() ([]byte, []int) {
	return file_aggregator_proto_rawDescGZIP(), []int{5}
}

func (x *UnsubscribeResponse) GetStatus() int32 {
	if x != nil {
		return x.Status
	}
	return 0
}

var File_aggregator_proto protoreflect.FileDescriptor

var file_aggregator_proto_rawDesc = []byte{
	0x0a, 0x10, 0x61, 0x67, 0x67, 0x72, 0x65, 0x67, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x70, 0x72, 0x6f,
	0x74, 0x6f, 0x12, 0x18, 0x75, 0x63, 0x61, 0x62, 0x2e, 0x73, 0x71, 0x61, 0x2e, 0x77, 0x6f, 0x72,
	0x6b, 0x69, 0x74, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x22, 0x2d, 0x0a, 0x15,
	0x52, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x53, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x4d, 0x65,
	0x73, 0x73, 0x61, 0x67, 0x65, 0x12, 0x14, 0x0a, 0x05, 0x67, 0x72, 0x6f, 0x75, 0x70, 0x18, 0x01,
	0x20, 0x01, 0x28, 0x09, 0x52, 0x05, 0x67, 0x72, 0x6f, 0x75, 0x70, 0x22, 0x2c, 0x0a, 0x16, 0x52,
	0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x53, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x52, 0x65, 0x73,
	0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12, 0x12, 0x0a, 0x04, 0x68, 0x6f, 0x73, 0x74, 0x18, 0x01, 0x20,
	0x01, 0x28, 0x09, 0x52, 0x04, 0x68, 0x6f, 0x73, 0x74, 0x22, 0x45, 0x0a, 0x11, 0x41, 0x64, 0x64,
	0x53, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x12, 0x14,
	0x0a, 0x05, 0x67, 0x72, 0x6f, 0x75, 0x70, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52, 0x05, 0x67,
	0x72, 0x6f, 0x75, 0x70, 0x12, 0x1a, 0x0a, 0x08, 0x63, 0x61, 0x70, 0x61, 0x63, 0x69, 0x74, 0x79,
	0x18, 0x03, 0x20, 0x01, 0x28, 0x05, 0x52, 0x08, 0x63, 0x61, 0x70, 0x61, 0x63, 0x69, 0x74, 0x79,
	0x22, 0x2c, 0x0a, 0x12, 0x41, 0x64, 0x64, 0x53, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x52, 0x65,
	0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12, 0x16, 0x0a, 0x06, 0x73, 0x74, 0x61, 0x74, 0x75, 0x73,
	0x18, 0x03, 0x20, 0x01, 0x28, 0x05, 0x52, 0x06, 0x73, 0x74, 0x61, 0x74, 0x75, 0x73, 0x22, 0x14,
	0x0a, 0x12, 0x55, 0x6e, 0x73, 0x75, 0x62, 0x73, 0x63, 0x72, 0x69, 0x62, 0x65, 0x4d, 0x65, 0x73,
	0x73, 0x61, 0x67, 0x65, 0x22, 0x2d, 0x0a, 0x13, 0x55, 0x6e, 0x73, 0x75, 0x62, 0x73, 0x63, 0x72,
	0x69, 0x62, 0x65, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12, 0x16, 0x0a, 0x06, 0x73,
	0x74, 0x61, 0x74, 0x75, 0x73, 0x18, 0x01, 0x20, 0x01, 0x28, 0x05, 0x52, 0x06, 0x73, 0x74, 0x61,
	0x74, 0x75, 0x73, 0x32, 0xdd, 0x02, 0x0a, 0x11, 0x53, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x41,
	0x67, 0x67, 0x72, 0x65, 0x67, 0x61, 0x74, 0x6f, 0x72, 0x12, 0x73, 0x0a, 0x0e, 0x52, 0x65, 0x71,
	0x75, 0x65, 0x73, 0x74, 0x53, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x12, 0x2f, 0x2e, 0x75, 0x63,
	0x61, 0x62, 0x2e, 0x73, 0x71, 0x61, 0x2e, 0x77, 0x6f, 0x72, 0x6b, 0x69, 0x74, 0x2e, 0x70, 0x72,
	0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2e, 0x52, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x53, 0x65,
	0x72, 0x76, 0x69, 0x63, 0x65, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x1a, 0x30, 0x2e, 0x75,
	0x63, 0x61, 0x62, 0x2e, 0x73, 0x71, 0x61, 0x2e, 0x77, 0x6f, 0x72, 0x6b, 0x69, 0x74, 0x2e, 0x70,
	0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2e, 0x52, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x53,
	0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12, 0x67,
	0x0a, 0x0a, 0x41, 0x64, 0x64, 0x53, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x12, 0x2b, 0x2e, 0x75,
	0x63, 0x61, 0x62, 0x2e, 0x73, 0x71, 0x61, 0x2e, 0x77, 0x6f, 0x72, 0x6b, 0x69, 0x74, 0x2e, 0x70,
	0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2e, 0x41, 0x64, 0x64, 0x53, 0x65, 0x72, 0x76, 0x69,
	0x63, 0x65, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x1a, 0x2c, 0x2e, 0x75, 0x63, 0x61, 0x62,
	0x2e, 0x73, 0x71, 0x61, 0x2e, 0x77, 0x6f, 0x72, 0x6b, 0x69, 0x74, 0x2e, 0x70, 0x72, 0x6f, 0x74,
	0x6f, 0x62, 0x75, 0x66, 0x2e, 0x41, 0x64, 0x64, 0x53, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x52,
	0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12, 0x6a, 0x0a, 0x0b, 0x55, 0x6e, 0x73, 0x75, 0x62,
	0x73, 0x63, 0x72, 0x69, 0x62, 0x65, 0x12, 0x2c, 0x2e, 0x75, 0x63, 0x61, 0x62, 0x2e, 0x73, 0x71,
	0x61, 0x2e, 0x77, 0x6f, 0x72, 0x6b, 0x69, 0x74, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75,
	0x66, 0x2e, 0x55, 0x6e, 0x73, 0x75, 0x62, 0x73, 0x63, 0x72, 0x69, 0x62, 0x65, 0x4d, 0x65, 0x73,
	0x73, 0x61, 0x67, 0x65, 0x1a, 0x2d, 0x2e, 0x75, 0x63, 0x61, 0x62, 0x2e, 0x73, 0x71, 0x61, 0x2e,
	0x77, 0x6f, 0x72, 0x6b, 0x69, 0x74, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2e,
	0x55, 0x6e, 0x73, 0x75, 0x62, 0x73, 0x63, 0x72, 0x69, 0x62, 0x65, 0x52, 0x65, 0x73, 0x70, 0x6f,
	0x6e, 0x73, 0x65, 0x42, 0x25, 0x0a, 0x18, 0x75, 0x63, 0x61, 0x62, 0x2e, 0x73, 0x71, 0x61, 0x2e,
	0x77, 0x6f, 0x72, 0x6b, 0x69, 0x74, 0x2e, 0x70, 0x72, 0x6f, 0x62, 0x6f, 0x62, 0x75, 0x66, 0x50,
	0x01, 0x5a, 0x07, 0x2e, 0x2f, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x06, 0x70, 0x72, 0x6f, 0x74,
	0x6f, 0x33,
}

var (
	file_aggregator_proto_rawDescOnce sync.Once
	file_aggregator_proto_rawDescData = file_aggregator_proto_rawDesc
)

func file_aggregator_proto_rawDescGZIP() []byte {
	file_aggregator_proto_rawDescOnce.Do(func() {
		file_aggregator_proto_rawDescData = protoimpl.X.CompressGZIP(file_aggregator_proto_rawDescData)
	})
	return file_aggregator_proto_rawDescData
}

var file_aggregator_proto_msgTypes = make([]protoimpl.MessageInfo, 6)
var file_aggregator_proto_goTypes = []interface{}{
	(*RequestServiceMessage)(nil),  // 0: ucab.sqa.workit.protobuf.RequestServiceMessage
	(*RequestServiceResponse)(nil), // 1: ucab.sqa.workit.protobuf.RequestServiceResponse
	(*AddServiceMessage)(nil),      // 2: ucab.sqa.workit.protobuf.AddServiceMessage
	(*AddServiceResponse)(nil),     // 3: ucab.sqa.workit.protobuf.AddServiceResponse
	(*UnsubscribeMessage)(nil),     // 4: ucab.sqa.workit.protobuf.UnsubscribeMessage
	(*UnsubscribeResponse)(nil),    // 5: ucab.sqa.workit.protobuf.UnsubscribeResponse
}
var file_aggregator_proto_depIdxs = []int32{
	0, // 0: ucab.sqa.workit.protobuf.ServiceAggregator.RequestService:input_type -> ucab.sqa.workit.protobuf.RequestServiceMessage
	2, // 1: ucab.sqa.workit.protobuf.ServiceAggregator.AddService:input_type -> ucab.sqa.workit.protobuf.AddServiceMessage
	4, // 2: ucab.sqa.workit.protobuf.ServiceAggregator.Unsubscribe:input_type -> ucab.sqa.workit.protobuf.UnsubscribeMessage
	1, // 3: ucab.sqa.workit.protobuf.ServiceAggregator.RequestService:output_type -> ucab.sqa.workit.protobuf.RequestServiceResponse
	3, // 4: ucab.sqa.workit.protobuf.ServiceAggregator.AddService:output_type -> ucab.sqa.workit.protobuf.AddServiceResponse
	5, // 5: ucab.sqa.workit.protobuf.ServiceAggregator.Unsubscribe:output_type -> ucab.sqa.workit.protobuf.UnsubscribeResponse
	3, // [3:6] is the sub-list for method output_type
	0, // [0:3] is the sub-list for method input_type
	0, // [0:0] is the sub-list for extension type_name
	0, // [0:0] is the sub-list for extension extendee
	0, // [0:0] is the sub-list for field type_name
}

func init() { file_aggregator_proto_init() }
func file_aggregator_proto_init() {
	if File_aggregator_proto != nil {
		return
	}
	if !protoimpl.UnsafeEnabled {
		file_aggregator_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*RequestServiceMessage); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_aggregator_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*RequestServiceResponse); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_aggregator_proto_msgTypes[2].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*AddServiceMessage); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_aggregator_proto_msgTypes[3].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*AddServiceResponse); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_aggregator_proto_msgTypes[4].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*UnsubscribeMessage); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_aggregator_proto_msgTypes[5].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*UnsubscribeResponse); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_aggregator_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   6,
			NumExtensions: 0,
			NumServices:   1,
		},
		GoTypes:           file_aggregator_proto_goTypes,
		DependencyIndexes: file_aggregator_proto_depIdxs,
		MessageInfos:      file_aggregator_proto_msgTypes,
	}.Build()
	File_aggregator_proto = out.File
	file_aggregator_proto_rawDesc = nil
	file_aggregator_proto_goTypes = nil
	file_aggregator_proto_depIdxs = nil
}
