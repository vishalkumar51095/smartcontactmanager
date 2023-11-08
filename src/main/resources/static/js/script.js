console.log("this is script file")

const toggleSidebar = () => {
    const $sidebar = $(".sidebar");
    const $content = $(".content");

    if ($sidebar.is(":visible")) {
        // Hide the sidebar
        $sidebar.hide();
        $content.css("margin-left", "0%");
    } else {
        // Show the sidebar
        $sidebar.show();
        $content.css("margin-left", "20%"); // You can adjust the margin as needed
    }
};

const search=()=>{
//    console.log("searching...")

    let query= $("#search-input").val()
    console.log(query);

    if(query == ""){
        $(".search-result").hide();
    }
    else{
        //search
        console.log(query);

        let url=`http://localhost:8585/search/${query}`;

        fetch(url).then(response=>{
            return response.json();
        })
        .then((data) => {

            //data.....
//            console.log(data);

            let text=`<div class='list-group'>`

            data.forEach(contact =>{

                text+=`<a href='/user/${contact.cId}/contact' class='list-gruop-item list-group-item-action'>${contact.name} </a>`

            });

            text+=`</div>`;
            $(".search-result").html(text);
            $(".search-result").show();


        });

    }
}