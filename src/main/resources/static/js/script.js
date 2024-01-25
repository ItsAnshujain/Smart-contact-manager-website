const toggleSidebar=()=>{
    if($(".sidebar").is(":visible")){
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    }
    else{
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};
const search=()=>{
    let query=$("#search-input").val();
    if(query==''){
        $(".search-result").hide();
    }
    else{
        let url=`http://localhost:8080/search/${query}`;
        fetch(url).then((response)=>{
            return response.json();
        }).then((data)=>{
            let text=`<div class='list-group'>`;

            data.forEach((contact) => {
                text+=`<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'>${contact.name}</a>`
            });

            text+=`</div>`
            $(".search-result").html(text);
            $(".search-result").show();
        });
    }
};

// first request to server to create order
const paymentStart=()=>{
    let amount=$("#payment_id").val();
    if(amount=='' || amount==null){
        // alert('amount is required');
        swal("Oops!", "Amount is required !!", "error")
        return;
    }
    // we are using ajax
    $.ajax({
        url:'/user/create_order',
        data: JSON.stringify({amount:amount, info:'order_request'}),
        contentType:'application/json',
        type:'POST',
        dataType:'json',
        success:function(response){
            // invoked when success
            console.log(response);
            if(response.status=='created'){
                let options = {
                    key: "rzp_test_sd5Mg54qetJVZR", 
                    amount: response.amount, 
                    currency: "INR",
                    name: "Smart Contact Manager",
                    description: "Donation",
                    image: "https://m.media-amazon.com/images/I/71ZGdWYW-pL._AC_UL320_.jpg",
                    order_id: response.id, 
                    handler: function (response){
                        console.log(response.razorpay_payment_id);
                        console.log(response.razorpay_order_id);
                        console.log(response.razorpay_signature);
                        // alert("Congrats !! Payment successfull..")
                        updataPaymentOnServer(
                            response.razorpay_payment_id, response.razorpay_order_id, "paid"
                        );
                        swal("Congrats!", "Payment successfull !!", "success")
                    },
                    prefill: {
                        name: "",
                        email: "",
                        contact: ""
                    },
                    notes: {
                        address: "Anshujain's office"
                    },
                    theme: {
                        color: "#5F8670"
                    }
                };
                var rzp = new Razorpay(options);
                rzp.on('payment.failed', function (response){
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    // alert('Oops payment failed..')
                    swal("Oops!", "Oops payment failed !!", "error")
                });
                rzp.open();
            }
        },
        error:function(error){
            // invoked when error
            console.log(error);
            // alert("Something went wrong");
            swal("Oops!", "Something went wrong !!", "error")
        }
    })
}
function updataPaymentOnServer(payment_id, order_id, status)
{
    $.ajax({
        url:'/user/update_order',
        data: JSON.stringify({payment_id:payment_id, order_id:order_id, status:status}),
        contentType:'application/json',
        type:'POST',
        dataType:'json',
        success:function(response){
            // invoked when success
            swal("Congrats!", "Payment successfull !!", "success")    
        },
        error:function(error){
            // invoked when error
            swal("Oops!", "Your payment is successfull, but we didn't get on server, we will contact you as soon as possible !!", "error")
        }
    })
}