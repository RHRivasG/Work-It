// Code generated by protoc-gen-go-grpc. DO NOT EDIT.
// versions:
// - protoc-gen-go-grpc v1.2.0
// - protoc             v3.19.2
// source: aggregator.proto

package proto

import (
	context "context"
	grpc "google.golang.org/grpc"
	codes "google.golang.org/grpc/codes"
	status "google.golang.org/grpc/status"
)

// This is a compile-time assertion to ensure that this generated file
// is compatible with the grpc package it is being compiled against.
// Requires gRPC-Go v1.32.0 or later.
const _ = grpc.SupportPackageIsVersion7

// ServiceAggregatorClient is the client API for ServiceAggregator service.
//
// For semantics around ctx use and closing/ending streaming RPCs, please refer to https://pkg.go.dev/google.golang.org/grpc/?tab=doc#ClientConn.NewStream.
type ServiceAggregatorClient interface {
	RequestService(ctx context.Context, in *RequestServiceMessage, opts ...grpc.CallOption) (*RequestServiceResponse, error)
	AddService(ctx context.Context, in *AddServiceMessage, opts ...grpc.CallOption) (*AddServiceResponse, error)
	RemoveService(ctx context.Context, in *RemoveServiceMessage, opts ...grpc.CallOption) (*RemoveServiceResponse, error)
}

type serviceAggregatorClient struct {
	cc grpc.ClientConnInterface
}

func NewServiceAggregatorClient(cc grpc.ClientConnInterface) ServiceAggregatorClient {
	return &serviceAggregatorClient{cc}
}

func (c *serviceAggregatorClient) RequestService(ctx context.Context, in *RequestServiceMessage, opts ...grpc.CallOption) (*RequestServiceResponse, error) {
	out := new(RequestServiceResponse)
	err := c.cc.Invoke(ctx, "/ucab.sqa.workit.protobuf.ServiceAggregator/RequestService", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *serviceAggregatorClient) AddService(ctx context.Context, in *AddServiceMessage, opts ...grpc.CallOption) (*AddServiceResponse, error) {
	out := new(AddServiceResponse)
	err := c.cc.Invoke(ctx, "/ucab.sqa.workit.protobuf.ServiceAggregator/AddService", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *serviceAggregatorClient) RemoveService(ctx context.Context, in *RemoveServiceMessage, opts ...grpc.CallOption) (*RemoveServiceResponse, error) {
	out := new(RemoveServiceResponse)
	err := c.cc.Invoke(ctx, "/ucab.sqa.workit.protobuf.ServiceAggregator/RemoveService", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

// ServiceAggregatorServer is the server API for ServiceAggregator service.
// All implementations must embed UnimplementedServiceAggregatorServer
// for forward compatibility
type ServiceAggregatorServer interface {
	RequestService(context.Context, *RequestServiceMessage) (*RequestServiceResponse, error)
	AddService(context.Context, *AddServiceMessage) (*AddServiceResponse, error)
	RemoveService(context.Context, *RemoveServiceMessage) (*RemoveServiceResponse, error)
	mustEmbedUnimplementedServiceAggregatorServer()
}

// UnimplementedServiceAggregatorServer must be embedded to have forward compatible implementations.
type UnimplementedServiceAggregatorServer struct {
}

func (UnimplementedServiceAggregatorServer) RequestService(context.Context, *RequestServiceMessage) (*RequestServiceResponse, error) {
	return nil, status.Errorf(codes.Unimplemented, "method RequestService not implemented")
}
func (UnimplementedServiceAggregatorServer) AddService(context.Context, *AddServiceMessage) (*AddServiceResponse, error) {
	return nil, status.Errorf(codes.Unimplemented, "method AddService not implemented")
}
func (UnimplementedServiceAggregatorServer) RemoveService(context.Context, *RemoveServiceMessage) (*RemoveServiceResponse, error) {
	return nil, status.Errorf(codes.Unimplemented, "method RemoveService not implemented")
}
func (UnimplementedServiceAggregatorServer) mustEmbedUnimplementedServiceAggregatorServer() {}

// UnsafeServiceAggregatorServer may be embedded to opt out of forward compatibility for this service.
// Use of this interface is not recommended, as added methods to ServiceAggregatorServer will
// result in compilation errors.
type UnsafeServiceAggregatorServer interface {
	mustEmbedUnimplementedServiceAggregatorServer()
}

func RegisterServiceAggregatorServer(s grpc.ServiceRegistrar, srv ServiceAggregatorServer) {
	s.RegisterService(&ServiceAggregator_ServiceDesc, srv)
}

func _ServiceAggregator_RequestService_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(RequestServiceMessage)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(ServiceAggregatorServer).RequestService(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/ucab.sqa.workit.protobuf.ServiceAggregator/RequestService",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(ServiceAggregatorServer).RequestService(ctx, req.(*RequestServiceMessage))
	}
	return interceptor(ctx, in, info, handler)
}

func _ServiceAggregator_AddService_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(AddServiceMessage)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(ServiceAggregatorServer).AddService(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/ucab.sqa.workit.protobuf.ServiceAggregator/AddService",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(ServiceAggregatorServer).AddService(ctx, req.(*AddServiceMessage))
	}
	return interceptor(ctx, in, info, handler)
}

func _ServiceAggregator_RemoveService_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(RemoveServiceMessage)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(ServiceAggregatorServer).RemoveService(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/ucab.sqa.workit.protobuf.ServiceAggregator/RemoveService",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(ServiceAggregatorServer).RemoveService(ctx, req.(*RemoveServiceMessage))
	}
	return interceptor(ctx, in, info, handler)
}

// ServiceAggregator_ServiceDesc is the grpc.ServiceDesc for ServiceAggregator service.
// It's only intended for direct use with grpc.RegisterService,
// and not to be introspected or modified (even as a copy)
var ServiceAggregator_ServiceDesc = grpc.ServiceDesc{
	ServiceName: "ucab.sqa.workit.protobuf.ServiceAggregator",
	HandlerType: (*ServiceAggregatorServer)(nil),
	Methods: []grpc.MethodDesc{
		{
			MethodName: "RequestService",
			Handler:    _ServiceAggregator_RequestService_Handler,
		},
		{
			MethodName: "AddService",
			Handler:    _ServiceAggregator_AddService_Handler,
		},
		{
			MethodName: "RemoveService",
			Handler:    _ServiceAggregator_RemoveService_Handler,
		},
	},
	Streams:  []grpc.StreamDesc{},
	Metadata: "aggregator.proto",
}
