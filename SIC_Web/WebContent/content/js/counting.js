var isInCount = false;
var customer;
var countType;
var custProducts;
var custManProducts;
var custMaraManProducts;
var custProductMap = [];
var shownProducts;
var invCount;
var partialCountCounter = 0;
//var countContinue;
var lastSelectedProduct;
var lastSelectedCustomer;
var sortType, sortDir;
var linesCounted;
var approvers;
var customers;
var currentProductId = -1;
var parentProductLot, parentProductFloor, parentProductSubLot;
var currentFoundProductHandler = null;
var itemToVerfiy = null;
var inVerification = false;
var calcValueBeforeCancel = null;
var subProductList = [];

function sortProducts(prod1, prod2) {
    var p1 = "", p2 = "";
    switch (sortType) {
        case "id": p1 = prod1.custProductId.toLowerCase();
            p2 = prod2.custProductId.toLowerCase();
            break;
        case "desc": p1 = prod1.custProductDesc.toLowerCase();
            p2 = prod2.custProductDesc.toLowerCase();
            break;
        case "lot1": p1 = prod1.lot1;
            p2 = prod2.lot1;
            break;
        case "lot2": p1 = prod1.lot2;
            p2 = prod2.lot2;
            break;
        case "lot3": p1 = prod1.lot3;
            p2 = prod2.lot3;
            break;
        case "stdQuantity": p1 = prod1.stdQuantity;
            p2 = prod2.stdQuantity;
            break;
        case "qty": p1 = $("#qty_" + prod1.id).val();
            p2 = $("#qty_" + prod2.id).val();
            break;
    }



    if (sortDir == "DESC") {
        if (p1 < p2) //sort string ascending
            return -1;
        if (p1 > p2)
            return 1;
        return 0; //default return value (no sorting)
    }
    else {
        if (p1 > p2) //sort string ascending
            return -1;
        if (p1 < p2)
            return 1;
        return 0; //default return value (no sorting)
    }
}


function CountCustProduct(prod) {
    var product = null;
    var qty = null;
    var oreredQty = 0;
    var countTime = null;

    function ctor() {
        product = prod;
    }

    ctor();

    this.setProduct = function (prod) {
        product = prod;
    };

    this.getProduct = function () {
        return product;
    };

    this.setQty = function (argQty) {
        qty = argQty;
    };

    this.getQty = function () {
        return qty;
    };

    this.setOrderedQty = function (argOrderedQty) {
        oreredQty = argOrderedQty;
    };

    this.getOrderedQty = function () {
        return oreredQty;
    };

    this.getCountTime = function () {
        return countTime;
    };

    this.setCountTime = function (argCountTime) {
        countTime = argCountTime;
    };
}

$(function () {
    $('#homeButton').click(function () {
        if (isInCount) {
            $('#warnPopup #msg').html("?????????? ????????????, ?????? ?????????? ???????????? ???????? ?");
            $('#warnPopup').dialog("option", "buttons", [{
                text: "??????????", click: function () {
                    $(this).dialog("close");
                    //saveCount(false);
                    location.href = "index.html";

                }
            }, {
                text: "??????????", click: function () {
                    $(this).dialog("close");
                }
            }]).dialog("open");

        }
        else {
            location.href = "index.html";
        }
    });

    $('#txtSelectCustomer').forceNumericOnly().bind('keypress', function (e) {
        var key = e.charCode || e.keyCode || 0;
        if (key == 13) {
            searchCustomer();
        }
        else {
            $('#customerDesc').html('');
        }
    });

    $('#btnSelectCustomer').click(searchCustomer);
    $('#startButton').click(startCount);
    $('#finishButton').click(endCount);
    $('#cancelButton').click(cancelCountClick);
    $('#hidProduct').bind('keypress', function (e) {
        var key = e.charCode || e.keyCode || 0;
        if (key == 13) {
            searchManProduct($.trim($(this).val()).toUpperCase());
        }
    });

    $("#itemsPopup").dialog({
        dialogClass: 'items',
        title: '???????????? ????????????',
        autoOpen: false,
        resizable: false,
        height: 330,
        width: 500,
        modal: true,
        buttons: {
            "??????????": function () {
                if (lastSelectedProduct == "") {
                    alert("?????? ?????? ????????.");
                    return;
                }
                //handleOneProductFound(custProductMap[lastSelectedProduct].getProduct());
                if (currentFoundProductHandler != null) {
                    currentFoundProductHandler.call(this, custProductMap[lastSelectedProduct].getProduct());
                }
                $(this).dialog("close");
            },
            "??????????": function () {
                $(this).dialog("close");
                focusOnProduct();
            }
        },
        //beforeClose:closeLoading,
        open: function () {
            $buttonPane = $(this).next();
            $buttonPane.find('button:first').addClass('dialog-button').addClass('close-button2');
            $buttonPane.find('button:last').addClass('dialog-button').addClass('ok-button');
        }
    });

    $("#itemPopup").dialog({
        dialogClass: 'item',
        title: '',
        autoOpen: false,
        resizable: false,
        height: 350,
        width: 400,
        modal: false,
        buttons: {
            "??????????": function () {
                $(this).dialog("close");
            }
        },
        open: function () {
            $buttonPane = $(this).next();
            $buttonPane.find('button:last').addClass('dialog-button').addClass('ok-button');
        }
    });


    $("#customersPopup").dialog({
        dialogClass: 'customers',
        title: '',
        autoOpen: false,
        resizable: false,
        height: 330,
        width: 450,
        modal: true,
        buttons: {
            "??????????": function () {
                if (lastSelectedCustomer == "") {
                    alert("?????? ?????? ????????.");
                    return;
                }
                customer = customers[lastSelectedCustomer];
                getManProductsForCustomer();
                $(this).dialog("close");
            },
            "??????????": function () {
                $(this).dialog("close");
            }
        },
        //beforeClose:closeLoading,
        open: function () {
            $buttonPane = $(this).next();
            $buttonPane.find('button:first').addClass('dialog-button').addClass('close-button2');
            $buttonPane.find('button:last').addClass('dialog-button').addClass('ok-button');
        }
    });

    $('#verificationPopup').dialog({
        dialogClass: 'verification-popup',
        title: '??????????',
        autoOpen: false,
        resizable: false,
        height: 320,
        width: 500,
        modal: true,
        buttons: {
            "??????????": function () {
                $(this).dialog("close");
                var keyboard = $('#qty_' + currentProductId).keyboard().getkeyboard();
                keyboard.reveal();

            }
        },
        beforeClose: function () {
            inVerification = false;
        },
        open: function () {
            $('#spnMessage').html('');
            inVerification = true;
            setTimeout(function () { $('#txtVerProduct').val('').focus(); }, 0);
        }

    });


    $("#warnPopup").dialog({
        dialogClass: 'warning',
        title: '??????????',
        autoOpen: false,
        resizable: false,
        height: 200,
        width: 250,
        modal: true,
        open: function () {
            $buttonPane = $(this).next();
            $buttonPane.find('button:first').addClass('dialog-button').addClass('ok-button-2');
        }
    });

    $("#errorPopup").dialog({
        dialogClass: 'error',
        title: '??????????',
        autoOpen: false,
        resizable: false,
        height: 200,
        width: 250,
        modal: true,
        buttons: {
            "??????????": function () {
                $(this).dialog("close");
            }
        },
        open: function () {
            $buttonPane = $(this).next();
            $buttonPane.find('button:first').addClass('dialog-button').addClass('ok-button-2');
        }
    });

    $("#infoPopup").dialog({
        dialogClass: 'info',
        title: '??????????',
        autoOpen: false,
        resizable: false,
        height: 200,
        width: 250,
        modal: true,
        open: function () {
            $buttonPane = $(this).next();
            $buttonPane.find('button:first').addClass('dialog-button').addClass('ok-button-2');
        }
    });


    $("#approverPopup").dialog({
        dialogClass: 'approver',
        title: '????????',
        autoOpen: false,
        resizable: false,
        height: 360,
        width: 350,
        position: "top",
        modal: true,
        buttons: {
            "??????": function () {
                approveFinishCount();

            },
            "????????": function () {
                $(this).dialog("close");
            }
        }
    });

    $('.sigPad').signaturePad({ defaultAction: "drawIt" });


    $('#empPass').bind('keypress', function (e) {
        var key = e.charCode || e.keyCode || 0;
        if (key == 13) {
            approveFinishCount();
        }
    });

    // get the cursor back on id product for the barcod reader
    $(document).bind('keydown', function (e) {
        if (isInCount && $(":focus").length == 0) {
            if (inVerification) {
                $('#txtVerProduct').focus();
            }
            else {
                $('#hidProduct').focus();
            }
        }
    });


    $.keyboard.keyaction.?????????? = function (base) {
        var keyboard = $('#qty_' + currentProductId).keyboard().getkeyboard();
        keyboard.close();

        $('#verificationPopup').dialog('open');
        $('#parentItem').html('???????? ???? - ' + custProductMap[currentProductId].getProduct().custProductId + " - " + custProductMap[currentProductId].getProduct().custProductDesc);


    };

    $('#txtVerProduct').bind('keypress', function (e) {
        var key = e.charCode || e.keyCode || 0;
        if (key == 13) {
            verifyProduct(currentProductId, $('#txtVerProduct').val());
            $('#txtVerProduct').val('').focus();
        }
    });


    $(window).unload(function () {
        if (isInCount) {
            saveCount(false);
        }
    });

    getApprovers();


});

function getApprovers() {
    var url = restManager.REST_END_POINT.APPROVERS;
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetApproversOk);
}

function ajaxGetApproversOk(data) {
    approvers = eval(data);
    for (var i = 0; i < approvers.length; i++) {
        $('#empName').append("<option value='" + approvers[i].id + "'>" + approvers[i].id + "</option>");
    }

    $('#txtSelectCustomer').focus();
}


function searchCustomer() {
    $('#startButton').removeClass('start_button').addClass('start_button_disabled');
    var custNo;
    if ($('#txtSelectCustomer').val() == "") {
        custNo = "__EMPTY__";
    }
    else {
        //wrap customer no
        custNo = $('#txtSelectCustomer').val();
    }
    //search for customer
    var url = restManager.REST_END_POINT.SEARCH_CUSTOMER.replace("{hospitalId}", hospitalId).replace("{custNo}", custNo);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetCustomerOk);
}

function ajaxGetCustomerOk(data, textStatus, jqXHR) {
    customers = eval(data);
    if (customers == null || customers.length == 0) {
        $('#warnPopup #msg').html("????????/?????????? ???????? ??????????.");
        $('#warnPopup').dialog("option", "buttons", [{
            text: "??????????", click: function () {
                $(this).dialog("close");
                $('#txtSelectCustomer').focus();
            }
        }]).dialog("open");
        return;
    }
    if (customers.length == 1) {
        //found one
        customer = customers[0];
        getManProductsForCustomer();
        return;
    }
    fillCustomersTable();
}

function fillCustomersTable() {
    $('#customersTableBody').html("");
    lastSelectedCustomer = "";
    var trHtml;
    for (var i = 0; i < customers.length; i++) {
        trHtml = "<tr id='row_" + i + "'><td width='10%'><div class='select' id='selCustomer_" + i + "'></div></td>" +
		"<td>" + customers[i].id + "</td><td>" + customers[i].name + "</td></tr>";
        $('#customersTableBody').append(trHtml);
    }
    $('[id^="selCustomer_"]').bind('click', function () {
        var id = $(this).attr("id").substring(12);
        if (lastSelectedCustomer != "") {
            $('#row_' + lastSelectedCustomer).removeClass('selected');
        }
        lastSelectedCustomer = id;
        $('#row_' + lastSelectedCustomer).addClass('selected');
    });
    $("#customersPopup").dialog("open");
}

function getManProductsForCustomer() {
    var url = restManager.REST_END_POINT.SEARCH_MAN_PRODUCTS.replace("{hospitalId}", hospitalId).replace("{custNo}", customer.id);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetCustomerManProductsrOk);
}

function ajaxGetCustomerManProductsrOk(data, textStatus, jqXHR) {
    custManProducts = eval(data);
    getMaraManProductForCustomer();
}

function getMaraManProductForCustomer() {
    var url = restManager.REST_END_POINT.SEARCH_MARA_MAN_PRODUCTS.replace("{hospitalId}", hospitalId).replace("{custNo}", customer.id);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetCustomerMaraManProductsrOk);
}

function ajaxGetCustomerMaraManProductsrOk(data, textStatus, jqXHR) {
    custMaraManProducts = eval(data);
    getProductForCustomer();
}

function getProductForCustomer() {
    var url = restManager.REST_END_POINT.SEARCH_CUST_PRODUCTS.replace("{hospitalId}", hospitalId).replace("{custNo}", customer.id);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxGetCustomerProductsrOk);
}

function findProductByProductId(productId) {
    for (i = 0; i < custProducts.length; i++) {
        if (custProducts[i].productId == productId)
            return custProducts[i];
    }

    return null;
}


function prepareCustProductMap() {
    subProductList = [];
    for (var i = 0; i < custProducts.length; i++) {
        custProductMap[custProducts[i].id] = new CountCustProduct(custProducts[i]);

        //createSubPrductsList
        if (custProducts[i].mitadef == "??") {
            for (var j = 0; j < custProducts[i].subIds.length; j++) {
                var subProduct = findProductByProductId(custProducts[i].subIds[j]);
                if (subProduct && subProduct.mitadef == "??") {
                    var connectionItem = { prodId: custProducts[i].productId, subId: custProducts[i].subIds[j] }
                    subProductList.push(connectionItem);
                }
            }
        }

    }
}


function ajaxGetCustomerProductsrOk(data, textStatus, jqXHR) {
    var tempCustProducts = eval(data);
    custProducts = [];
    for (var i = 0; i < tempCustProducts.length; i++) {
        if (tempCustProducts[i].mitadef == '??') {
            custProducts[custProducts.length] = tempCustProducts[i];
        }
    }
    prepareCustProductMap();
    $('#txtSelectCustomer').val(customer.id);
    $('#customerDesc').html(customer.name).css('display', 'inline');



    if (customer.notTransmitedCountId) {
        if (customer.notTransmitedCountType == "FULL") {
            $('#warnPopup #msg').html("???? ???????? ???????? ?????????? ?????????? ????, ???? ???????????? ???????????? ?????????? ??????????????.");
            $('#warnPopup').dialog("option", "buttons", [{
                text: "??????????", click: function () {
                    $(this).dialog("close");
                    $('#txtSelectCustomer').val('').focus();
                }
            }]).dialog("open");
            cleanCustomer();
            return;
        }
        else {

            $('#warnPopup #msg').html("?????????? ???? ?????????? ?????????? ?????????? ???????? ??????????.?????? ???????????? ?????????");
            $('#warnPopup').dialog("option", "buttons", [{
                text: "??????????", click: function () {
                    $(this).dialog("close");
                    restartCounting();
                    $('#startButton').removeClass('start_button_disabled').addClass('start_button');
                }
            }, {
                text: "??????????", click: function () {
                    $(this).dialog("close");
                    cleanCustomer();
                    $('#txtSelectCustomer').focus();
                }
            }]).dialog("open");
        }
    }
    else if (customer.currentCountId > 0) {

        var msg = "?????????? ???? ???? ?????????? ???????? ";
        if (customer.currentCountType == "FULL") {
            msg += "???????? ";
        }
        else {
            msg += "??????????";
        }
        msg += ".?????? ???????????? ?????????????? ???? ???????????? ???????? ?????????";
        $('#warnPopup #msg').html(msg);
        $('#warnPopup').dialog({ width: 500 }).dialog("option", "buttons", [{
            text: "????????", click: function () {
                $(this).dialog("close");
                continueCounting();
                $('#startButton').removeClass('start_button_disabled').addClass('start_button');
            }
        },
		{
		    text: "????????", click: function () {
		        $(this).dialog("close");
		        deleteCounting();
		        $('#startButton').removeClass('start_button_disabled').addClass('start_button');
		    }
		},
		{
		    text: "??????????", click: function () {
		        $(this).dialog("close");
		        cleanCustomer();
		        $('#txtSelectCustomer').focus();
		    }
		}]).dialog("open");

    }
    else {
        $('#startButton').removeClass('start_button_disabled').addClass('start_button');
    }

}

function changeHeaderState(disable) {
    $('.header input').attr("disabled", disable);
}

function continueCounting() {
    //countContinue=true;
    countType = customer.currentCountType;
    if (countType == "FULL") {
        $('#radFull').attr('checked', true);
    }
    else {
        $('#radPartial').attr('checked', true);
    }
    changeHeaderState(true);
    var url = restManager.REST_END_POINT.GET_COUNT.replace("{countId}", customer.currentCountId);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxAddCountOk, ajaxAddCountFail);
}

function restartCounting() {
    //countContinue=true;
    countType = customer.notTransmitedCountType;
    if (countType == "FULL") {
        $('#radFull').attr('checked', true);
    }
    else {
        $('#radPartial').attr('checked', true);
    }
    changeHeaderState(true);
    var url = restManager.REST_END_POINT.RESTART_COUNT.replace("{countId}", customer.notTransmitedCountId);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxAddCountOk, ajaxAddCountFail);
}

function deleteCounting() {
    //countContinue=true;
    countType = customer.currentCountType;
    var url = restManager.REST_END_POINT.DELETE_COUNT.replace("{countId}", customer.currentCountId).replace("{countType}", countType);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, ajaxDeleteCountOk, ajaxDeleteCountFail);
}

function ajaxDeleteCountOk() {

}

function ajaxDeleteCountFail() {
    $('#errorPopup #msg').html("???????????? ???????????? ?????????? ??????????.");
    $('#errorPopup').dialog("open");
}

function startCount() {
    if (isInCount || $('#startButton').hasClass('start_button_disabled')) {
        return;
    }
    //countContinue=false;
    countType = $('input[name=radCountType]:checked').val();
    changeHeaderState(true);
    var url = restManager.REST_END_POINT.START_COUNT.replace("{hospitalId}", customer.hospital.id).replace("{customerId}", customer.id)
	.replace("{type}", countType);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.POST, restManager.AJAX_CONTENT_TYPE.JSON, ajaxAddCountOk, ajaxAddCountFail);
}

function ajaxAddCountFail() {
    isInCount = false;
    changeHeaderState(false);
    $('#errorPopup #msg').html("???????????? ???????????? ?????????? ?????????? ??????????.");
    $('#errorPopup').dialog("open");
}

function ajaxAddCountOk(data) {
    debugger;
    $('#countTableBody').html('');
    shownProducts = [];
    $('#finishButton').removeClass('finish_button_disabled').addClass('finish_button');
    $('#cancelButton').removeClass('cancel_button_disabled').addClass('cancel_button');
    $('#startButton').removeClass('start_button').addClass('start_button_disabled');
    invCount = eval(data);
    var d = new Date(invCount.countDate);
    var countDate = sicGeneralUtil.padd(d.getDate(), 2, '0', false) + "/" + sicGeneralUtil.padd(d.getMonth() + 1, 2, '0', false) + "/" + d.getFullYear();
    var countTime = sicGeneralUtil.padd(d.getHours(), 2, '0', false) + ":" + sicGeneralUtil.padd(d.getMinutes(), 2, '0', false);
    $('#countDate').html(countDate);
    $('#countTime').html(countTime);
    $('.counting-date .content').show();
    partialCountCounter = 0;
    isInCount = true;
    var linesCount = "";
    linesCounted = 0;
    //if(countContinue){
    //continue a count - draw all count lines
    if (invCount.lines != null) {
        var prod;
        linesCount = invCount.lines.length;
        for (var i = 0; i < invCount.lines.length; i++) {
            if (invCount.lines[i].product != null) {
                prod = custProductMap[invCount.lines[i].product.id];
                if (invCount.lines[i].countQty > -1) {
                    linesCounted++;
                    prod.setQty(invCount.lines[i].countQty);
                    prod.setOrderedQty(invCount.lines[i].orderQty);
                }
                shownProducts[shownProducts.length] = prod.getProduct();
                addProductToCount(prod.getProduct(), i, prod.getQty());
            }
        }
    }

    if (countType == "FULL") {
        $('#spnLinesTotal').html(linesCount);
        $('#spnLinesCounted').html(linesCounted);
        $('#linesDiv').show();
    }

    $('#countTableHolder').show();
    $('#hidProduct').show();
    focusOnProduct();
}

function attachNumPad(selector) {
    selector.keyboard({
        openOn: '',
        layout: 'custom',
        customLayout: { 'default': ['5 6 7 8 9', '0 1 2 3 4', '{a} . {b}  {c} {??????????}'] },
        restrictInput: true,
        preventPaste: true,
        autoAccept: false,
        lockInput: true,
        position: {
            of: $(window), // null (attach to input/textarea) or a jQuery object (attach elsewhere)
            my: 'center top',
            at: 'center top',
            at2: 'center top' // used when "usePreview" is false (centers keyboard at the bottom of the input/textarea)
        },
        accepted: function (e, keyboard, el) {
            if (isNaN($(el).val())) {
                handleCountLine($(el).attr('id').substr(4), $(el).val(), false);
                $(el).val('');
                $('#warnPopup #msg').html("???????? ???? ??????????.");
                $('#warnPopup').dialog("option", "buttons", [{
                    text: "??????????", click: function () {
                        $(this).dialog("close");
                        focusOnProduct();
                    }
                }]).dialog("open");
                return;
            }
            $('[id^=countRow_]').removeClass('incount');
            handleCountLine($(el).attr('id').substr(4), $(el).val(), false);
            focusOnProduct();
        },
        visible: function (e, k, el) {
            $('[name=??????????]').addClass('special-key');
            $('[id^=countRow_]').removeClass('incount');
            $('#countRow_' + $(el).attr('id').substr(4)).addClass('incount');
            currentProductId = $(el).attr('id').substr(4);
        },
        canceled: function (e, keyboard, el) {
            $('[id^=countRow_]').removeClass('incount');
            if (calcValueBeforeCancel) {
                $(el).val(calcValueBeforeCancel);
                calcValueBeforeCancel = null;
            }
            focusOnProduct();
        },
        beforeClose: function (e, keyboard, el) {
            calcValueBeforeCancel = $(el).val();
        }

    });

    $(selector).bind('focus', function () {
        var prodId = $(this).attr('id').substr(4);
        var line = custProductMap[prodId];
        var prod = line.getProduct();
        var $that = $(this);

        if (prod.comment) {
            $('#infoPopup #msg').html(prod.comment);
            $('#infoPopup').dialog("option", "buttons", [{
                text: "??????????", click: function () {
                    $(this).dialog("close");
                    $that.keyboard().getkeyboard().reveal();

                }
            }]).dialog("open");
            return;

        }
        else {
            $that.keyboard().getkeyboard().reveal();
        }
    });




}


function attachConfirmDialog(selector) {
    selector.bind('focus', function () {
        var id = $(this).attr('id').substr(4);
        var $this = $(this);
        $('#infoPopup #msg').html("?????? ???????????? ?????????");
        $('#infoPopup').dialog("option", "buttons", [{
            text: "????", click: function () {
                $(this).dialog("close");
                $this.val(0);
                handleCountLine(id, 0, false);
                $this.blur();
                focusOnProduct();

            }
        }, {
            text: "????", click: function () {
                $(this).dialog("close");
                $this.val(custProductMap[id].getProduct().stdQuantity);
                handleCountLine(id, custProductMap[id].getProduct().stdQuantity, true);
                $this.blur();
                focusOnProduct();
            }
        }]).dialog("open");
    });
}

function skip(id) {
    $('#qty_' + id).val(custProductMap[id].getProduct().stdQuantity);
    handleCountLine(id, custProductMap[id].getProduct().stdQuantity, true);
    focusOnProduct();
}


function sortTable(argSortType) {

    if (sortType == argSortType) {
        sortDir = (sortDir == "DESC") ? "ASC" : "DESC";
    }
    else {
        sortType = argSortType;
        sortDir = "DESC";
    }

    $('.table-header .sort').removeClass('sort_desc').removeClass('sort_asc').addClass('sort_regular');
    if (sortDir == "DESC") {
        $('.table-header #sort_' + argSortType).removeClass('sort_regular').addClass('sort_desc');
    }
    else {
        $('.table-header #sort_' + argSortType).removeClass('sort_regular').addClass('sort_asc');
    }
    shownProducts.sort(sortProducts);
    $('#countTableBody').html('');
    for (var i = 0; i < shownProducts.length; i++) {
        addProductToCount(shownProducts[i], i, custProductMap[shownProducts[i].id].getQty());
    }
}


function addProductToCount(prod, count, qty) {
    var trClass = (count % 2 == 0) ? "even" : "odd";
    if (qty != undefined) {
        trClass += " selected";
    }
    var manProd = "";
    /*for(var i=0;i<custManProducts.length;i++){
		if(custManProducts[i].custProductId==prod.custProductId){
			manProd=custManProducts[i].manProductId;
			break;
		}
	}*/
    for (var i = 0; i < custMaraManProducts.length; i++) {
        if (custMaraManProducts[i].productId == prod.productId) {

            manProd = custMaraManProducts[i].manProductId;
            break;

        }

    }

    var trHtml = "<tr id='countRow_" + prod.id + "' class='" + trClass + "'>" +
	"<td width='15%' class='text-center first' id='prodTd_" + count + "' title='" + prod.custProductId + "' onclick='showDescForItem(event,\"" + prod.id + "\")'>" + prod.custProductId + "</td>" +
	"<td width='29%' title='" + prod.custProductDesc + "'>" + prod.custProductDesc + "</td>" +
	"<td width='16%'  style='overflow: hidden;text-overflow: ellipsis;' title='" + manProd + "'>" + manProd + "</td>" +
	"<td width='9%' class='text-center'>" + prod.customerUnitDesc + "</td>" +
	"<td width='4%' class='text-center'>" + prod.lot2 + "</td>" +
	"<td width='4%' class='text-center'>" + prod.lot1 + "</td>" +
	"<td width='8%' class='text-center'>" + prod.stdQuantity + "</td>" +
	"<td width='15%' class='qty text-center'><input type='text' class='numpad' id='qty_" + prod.id + "'";

    if (qty != undefined) {
        trHtml += " value='" + qty + "'";
    }
    trHtml += "/>";

    if (prod.productType == "1") {
        trHtml += "<div class='skip' onclick='skip(" + prod.id + ")'></div>";
    }

    trHtml += "</td></tr>";
    $('#countTableBody').append(trHtml);
    if (prod.productType == "1") {
        attachNumPad($('#qty_' + prod.id));
    }
    else {
        attachConfirmDialog($('#qty_' + prod.id));
    }
}

function showDescForItem(e, id) {

    //$('#itemPopup #txtMiniItemCode').val(custProductMap[id].getProduct().custProductId);//elih31/07/14
    $('#itemPopup #txtMiniItemCode').val(custProductMap[id].getProduct().productId);
    $('#itemPopup #txtMiniItemStdQty').val(custProductMap[id].getProduct().stdQuantity);
    $('#itemPopup #txtMiniLot3').val(custProductMap[id].getProduct().lot3);
    $('#itemPopup #txtMiniLot1').val(custProductMap[id].getProduct().lot1);
    $('#itemPopup #txtDesc2').val(custProductMap[id].getProduct().custProductDesc2);

    //var td=$('#prodTd_' + index);
    $('#itemPopup').dialog("option", "position", [e.clientX - 300, e.clientY]).dialog('open');

}

function addAlltems() {
    for (var i = 0; i < custProducts.length; i++) {
        var cp = custProducts[i];
        shownProducts[shownProducts.length] = cp;
        addProductToCount(cp, i);
    }
}


function focusOnProduct() {
    setTimeout(function () { $('#hidProduct').val('').focus(); }, 0);
}

function searchManProduct(manProductId) {
    var foundProd = [];
    for (var i = 0; i < custManProducts.length; i++) {
        if (custManProducts[i].manProductId == manProductId) {
            foundProd[0] = custManProducts[i].custProductId;
            break;
        }
    }

    //if not found - check mara man product
    if (foundProd.length == 0) {
        for (var i = 0; i < custMaraManProducts.length; i++) {
            if (custMaraManProducts[i].manProductId == manProductId) {
                //find customer product for this swhm product
                for (var j = 0; j < custProducts.length; j++) {
                    if (custMaraManProducts[i].productId == custProducts[j].productId) {
                        foundProd[foundProd.length] = custProducts[j].custProductId;
                        break;
                    }
                }
            }
        }
    }

    if (foundProd.length == 0) {
        //not found yet - then you got the customer product to search for
        foundProd[0] = manProductId;
    }


    //proceed with new product id 
    currentFoundProductHandler = handleOneProductFound;
    searchProduct(foundProd, true, function () {
        $('#warnPopup #msg').html("???????? [" + foundProd + "] ???? ???????? ??????????.");
        $('#warnPopup').dialog("option", "buttons", [{
            text: "??????????", click: function () {
                $(this).dialog("close");
                focusOnProduct();
            }
        }]).dialog("open");
    });
}

function searchProduct(custProductId, checkQty, notFoundHandler) {
    var foundProducts = new Array();
    for (var j = 0; j < custProductId.length; j++) {
        for (var i = 0; i < custProducts.length; i++) {
            if (custProducts[i].mitadef == '??' && custProducts[i].custProductId == custProductId[j]) {
                foundProducts[foundProducts.length] = custProducts[i];
            }
        }
    }
    if (foundProducts.length == 0) {
        notFoundHandler.call();
        return;
    }
    if (foundProducts.length == 1) {
        currentFoundProductHandler.call(this, foundProducts[0]);
        return;
    }
    if (checkQty) {
        //chceck if w/o the counted product we do have ONE product
        var finalFoundProduct = [];
        for (var i = 0; i < foundProducts.length; i++) {
            if ($('#qty_' + foundProducts[i].id).val() == "") {
                finalFoundProduct[finalFoundProduct.length] = foundProducts[i];
            }
        }
        if (finalFoundProduct.length == 1) {
            currentFoundProductHandler.call(this, finalFoundProduct[0]);
            return;
        }
        if (finalFoundProduct.length == 0) {
            //fill items ih table
            fillItemsTable(foundProducts);
        }
        else {
            fillItemsTable(finalFoundProduct);
        }
    }
    else {
        fillItemsTable(foundProducts);
    }
}


function fillItemsTable(prods) {
    $('#itemsTableBody').html("");
    lastSelectedProduct = "";
    var trHtml;
    for (var i = 0; i < prods.length; i++) {
        trHtml = "<tr id='row_" + prods[i].id + "'><td width='10%'><div class='select' id='selItem_" + prods[i].id + "'></div></td>" +
		"<td width='20%'>" + prods[i].productId + "</td><td width='35%'><div class='fixed-width-220'>" + prods[i].custProductDesc + "</div></td>" +
		"<td style='text-align:center'>" + prods[i].lot1 + "-" + prods[i].lot2 + "-" + prods[i].lot3 + "</td></tr>";
        //"<td width='20%'>" + prods[i].custProductId + "</td><td width='40%'><div class='fixed-width-250'>" + prods[i].custProductDesc + "</div></td>" +
        //"<td style='text-align:center'>" + prods[i].lot1 + "-" + prods[i].lot2 + "-" + prods[i].lot3 +"---"+prods[i].productId+ "</td></tr>";
        $('#itemsTableBody').append(trHtml);
    }
    $('[id^="selItem"]').bind('click', function () {
        var id = $(this).attr("id").substring(8);
        if (lastSelectedProduct != "") {
            $('#row_' + lastSelectedProduct).removeClass('selected');
        }
        lastSelectedProduct = id;
        $('#row_' + lastSelectedProduct).addClass('selected');
    });
    $("#itemsPopup").dialog("open");
}

function handleOneProductFound(prod) {
    if (countType == "PARTIAL" && $('#qty_' + prod.id).length == 0) {
        shownProducts[shownProducts.length] = prod;
        addProductToCount(prod, ++partialCountCounter);
    }
    $('#qty_' + prod.id).focus();

    return;
}

function isAllCounted() {
    var res = new Array();

    for (var i = 0; i < custProducts.length; i++) {
        if ($('#qty_' + custProducts[i].id).val() == "") {
            var dupFound = false;
            //check if there's anther item in the samge mitadef group that was counted
            for (var j = 0; j < custProducts.length; j++) {
                if (custProducts[i].custProductId == custProducts[j].custProductId && custProducts[i].id != custProducts[j].id) {
                    if ($('#qty_' + custProducts[i].id).val() != "") {
                        dupFound = true;
                        break;
                    }
                }
            }
            if (!dupFound) {
                res[res.length] = custProducts[i];
            }
        }
    }
    return res;
}

function calcOrdQty(countedQty, minimalQty, stdQuantity) {
    var ordQty = (countedQty > stdQuantity) ? 0 : stdQuantity - countedQty;
    if (!minimalQty || ordQty == 0) {
        return ordQty;
    }
    else {
        if (ordQty <= minimalQty) {
            return minimalQty;
        }
        else {
            var div = Math.round(ordQty / minimalQty);
            return div * minimalQty;
        }
    }
}

function handleCountLine(id, qty, skip) {
    if (isNaN(qty) || qty == "") {
        $('#countRow_' + id).removeClass("selected");
        $('#spnLinesCounted').html(--linesCounted);
    }
    else {

        var line = custProductMap[id];
        var countedQty = parseFloat(qty);
        var stdQty = parseFloat(line.getProduct().stdQuantity);
        var minQty = line.getProduct().minimalOrderCount;
        if (line.getProduct().countFactor && !skip) {
            countedQty = parseFloat((countedQty * line.getProduct().countFactor).toFixed(2));
            $('#qty_' + id).val(countedQty);
        }

        if (countedQty > stdQty) {
            $('#warnPopup #msg').html("?????????? ?????????? ?????????? ??????????. ?????? ?????? ???????? ?????? ?????????? [" + countedQty + "]?");
            $('#warnPopup').dialog("option", "buttons", [{
                text: "??????????", click: function () {
                    $(this).dialog("close");
                    if (!$('#countRow_' + id).hasClass("selected")) {
                        $('#countRow_' + id).addClass("selected");
                        $('#spnLinesCounted').html(++linesCounted);
                    }

                    line.setQty(countedQty);
                    line.setCountTime(new Date().getTime());

                    var ordQty = calcOrdQty(countedQty, minQty, stdQty);
                    line.setOrderedQty(ordQty);
                    focusOnProduct();
                }
            },
			{
			    text: "??????????", click: function () {
			        $(this).dialog("close");
			        $('#qty_' + id).val('').focus();
			    }
			}]).dialog("open");
            return;

        }


        if (!$('#countRow_' + id).hasClass("selected")) {
            $('#countRow_' + id).addClass("selected");
            $('#spnLinesCounted').html(++linesCounted);
        }

        line.setQty(countedQty);
        line.setCountTime(new Date().getTime());

        var ordQty = calcOrdQty(countedQty, minQty, stdQty);
        line.setOrderedQty(ordQty);
    }
}



function saveCount(finish) {
    //prepare count lines
    var lines = new Array();
    var id;
    $('[id^="qty_"]').each(function () {
        id = $(this).attr('id').substr(4);
        if ((custProductMap[id].getQty() == null || custProductMap[id].getQty() === "") && finish) {
            return;
        }
        var line = {};
        line.product = custProductMap[id].getProduct();
        line.countQty = custProductMap[id].getQty();
        line.orderQty = custProductMap[id].getOrderedQty();
        line.countTime = custProductMap[id].getCountTime();
        if (line.countQty == null) {
            line.countQty = -1;
            line.orderQty = -1;
        }
        lines[lines.length] = line;
    });
    invCount.lines = lines;
    var url = restManager.REST_END_POINT.SAVE_COUNT.replace("{countId}", invCount.countId).replace("{finish}", finish);
    var okHandler = undefined, failHandler = undefined;
    if (finish) {
        okHandler = ajaxFinishCountOk;
        failHandler = ajaxFinishCountFail;
    }
    else {
        //okHandler=failHandler=function(){location.href="index.html";};
    }
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.PUT, restManager.AJAX_CONTENT_TYPE.JSON, okHandler, failHandler, invCount, undefined, false);
}


var lineProducts = [];

function IsExistInLineProducts(productId) {
    for (j = 0; j < lineProducts.length; j++) {
        if (lineProducts[j].productId == productId)
            return true;
    }

    return false;
}

function checkInsubProductList(productId) {
    for (z = 0; z < subProductList.length; z++) {
        if (subProductList[z].prodId == productId && !IsExistInLineProducts(subProductList[z].subId)) {
            return subProductList[z].subId;
        }
        if (subProductList[z].subId == productId && !IsExistInLineProducts(subProductList[z].prodId)) {
            return subProductList[z].prodId;
        }
    }
    return false;
}

function endCount() {
    if (!isInCount || $('#finishButton').hasClass('finish_button_disabled')) {
        return;
    }
    //if full count - check that everything is counted
    if (countType == "FULL") {
        var res = isAllCounted();
        if (res.length > 0) {
            $('#warnPopup #msg').html("???? ???????? ?????????? ?????????? ????????. ?????? ???????????????? ???? ??????????");
            $('#warnPopup').dialog("option", "buttons", [{
                text: "??????????", click: function () {
                    $(this).dialog("close");
                    focusOnProduct();
                }
            }]).dialog("open");
            return;
        }
    }
    else {
        var lineLen = $('[id^=qty_]').length;
        if (lineLen == 0) {
            $('#warnPopup #msg').html("???? ???????? ?????????? ?????????? ?????????? ?????? ?????????? ??????????.");
            $('#warnPopup').dialog("option", "buttons", [{
                text: "??????????", click: function () {
                    $(this).dialog("close");
                    focusOnProduct();
                }
            }]).dialog("open");
            return;
        }
    }
    if (subProductList.length > 0) {
        lineProducts = [];
        $('[id^="qty_"]').each(function () {
            id = $(this).attr('id').substr(4);
            lineProducts.push(custProductMap[id].getProduct());
        });
        for (var i = 0; i < lineProducts.length; i++) {
            var result = checkInsubProductList(lineProducts[i].productId);
            if (result) {
                var missingProd = findProductByProductId(result);
                $('#warnPopup #msg').html("???? ???????? ?????????? ?????????? ??????????. ?????????? " + missingProd.custProductId + " ???? ????????");
                $('#warnPopup').dialog("option", "buttons", [{
                    text: "??????????", click: function () {
                        $(this).dialog("close");
                    }
                }]).dialog("open");
                return;
            }
        }
    }
    $('#empPass').val('');
    $('#approverPopup').dialog("open");
}



function approveFinishCount() {
    var approver = null;
    for (var i = 0; i < approvers.length; i++) {
        if (approvers[i].id == $('#empName').val()) {
            approver = approvers[i];
            break;
        }
    }
    if ($('#empPass').val().toUpperCase() != approver.password) {
        $('#warnPopup #msg').html("?????????? ?????????? ??????????.");
        $('#warnPopup').dialog("option", "buttons", [{
            text: "??????????", click: function () {
                $(this).dialog("close");
            }
        }]).dialog("open");
        return;
    }
    invCount.approver = approver;
    saveCount(true);
}


function cancelCountClick() {
    if (!isInCount || $('#cancelButton').hasClass('cancel_button_disabled')) {
        return;
    }
    $('#warnPopup #msg').html("?????? ???????? ???? ?????????????");
    $('#warnPopup').dialog("option", "buttons", [{
        text: "??????????", click: function () {
            $(this).dialog("close");
            cancelCount();
        }
    }, {
        text: "??????????", click: function () {
            $(this).dialog("close");
        }
    }]).dialog("open");
}

function cancelCount() {
    var url = restManager.REST_END_POINT.CANCEL_COUNT.replace("{countId}", invCount.countId);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.DELETE, restManager.AJAX_CONTENT_TYPE.JSON, ajaxFinishCountOk);
}

function cleanCustomer() {
    customer = null;
    custProductMap = [];
    custProducts = null;
    custManProducts = null;
    $('#txtSelectCustomer').val("");
    $('#customerDesc').html("").hide();


}

function ajaxFinishCountOk() {
    $('#approverPopup').dialog("close");
    $('.sigPad').signaturePad().clearCanvas();
    changeHeaderState(false);
    isInCount = false;
    cleanCustomer();
    $('#txtSelectCustomer').focus();
    $('.counting-date .content').hide();
    $('#countTableBody').html('');
    $('#countTableHolder').hide();
    $('#hidProduct').hide();
    $('#linesDiv').hide();
    $('#finishButton').removeClass('finish_button').addClass('finish_button_disabled');
    $('#cancelButton').removeClass('cancel_button').addClass('cancel_button_disabled');
}

function ajaxFinishCountFail() {
    $('#approverPopup').dialog("close");
    $('#errorPopup #msg').html("???????? ?????????? ????????");
    $('#errorPopup').dialog("open");
}

function verifyProduct(parentProdId, prodId) {
    parentProductLot = custProductMap[parentProdId].getProduct().lot2;//parent item lot
    parentProductFloor = custProductMap[parentProdId].getProduct().lot1;//parent item floor
    parentProductSubLot = custProductMap[parentProdId].getProduct().lot3;//parent item sub lot
    itemToVerfiy = prodId;

    var foundProdId = prodId;
    for (var i = 0; i < custManProducts.length; i++) {
        if (custManProducts[i].manProductId == prodId) {
            foundProdId = custManProducts[i].custProductId;
            break;
        }
    }


    currentFoundProductHandler = function (prod) {
        checkSpecificProduct(prod, true);
    };

    searchProduct([foundProdId], false, function () {
        getGeneralProductForCustProduct(foundProdId);
    });

}

function findSubId(subProdId) {
    for (var i = 0; i < custProducts.length; i++) {
        if (custProducts[i].productId == subProdId) {
            return custProducts[i].getProduct();
        }
    }
    return null;
}

function checkSpecificProduct(product, inCart) {
    $('#spnMessage').html("").removeClass("info warn error");
    if (product == null) {
        $('#spnMessage').html("???????? [" + itemToVerfiy + "] ???? ???????? ?????????? ??????.").addClass("error");

        return;
    }
    if (inCart) {
        //check product lot
        if (product.lot1 == parentProductFloor && product.lot2 == parentProductLot && product.lot3 == parentProductSubLot) {
            //found it
            $('#spnMessage').html("???????? [" + itemToVerfiy + "] ???????? ???????????? ????????????.").addClass("info");
            return;
        }
        else {
            //different lot
            $('#spnMessage').html("?????????? [" + itemToVerfiy + "] ???????? ?????????? ???????????? ??????: <br/>????????:" + product.lot1 + "<br/>??????????: " + product.lot2 + "<br/>???? ??????????: " + product.lot3).addClass("warn");
            return;
        }
    }

    //check substitutes
    for (var subId in product.subIds) {
        var subProd = findSubId(subId);
        if (subProd != null) {
            //sub found in cart
            if (subProd.lot1 == parentProductFloor && subProd.lot2 == parentProductLot && subProd.lot3 == parentProductSubLot) {
                //found it
                $('#spnMessage').html("???????? [" + itemToVerfiy + "] ???????? ???????????? ????????????.").addClass("info");
                return;
            }
            else {
                //different lot
                $('#spnMessage').html("??????????  [" + itemToVerfiy + "] ???????? ?????????? ???????????? ??????,????????: " + subProd.lot1 + ", ??????????: " + subProd.lot2 + ",???? ??????????: " + subProd.lot3).addClass("warn");
                return;
            }
        }
    }
    //not found at all
    $('#spnMessage').html("???????? ???????????? ?????????? [" + itemToVerfiy + "] ???? ???????? ??????????.").addClass("error");

}

function getGeneralProductForCustProduct(prodId) {
    var url = restManager.REST_END_POINT.GET_GENERAL_PRODUCT.replace("{custProductId}", prodId);
    restManager.sendAjaxRequest(url, restManager.AJAX_REQUEST_TYPE.GET, restManager.AJAX_CONTENT_TYPE.JSON, function (data) {
        checkSpecificProduct(eval(data), false);
    });
}

