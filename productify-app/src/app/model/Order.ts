export interface Order {
    orderId: string;          
    userId: number;           
    userName: string;         
    totalAmount: number;      
    orderDate: Date;          
    deliveryDate?: Date;      
    status: string;           
    paymentMethod: string;    
    shippingAddress: string;  
    contactNumber: string;    
    trackingNumber?: string;
  }