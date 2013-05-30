Ext.define('SimpsonStore', {
	extend : 'Ext.data.Store',
	fields : [ 'name', 'email', 'phone', 'notes' ],
	proxy : {
		type : 'ajax',
		url : 'api/users',
		reader : {
			type : 'json',
			root : 'results',
			totalProperty : 'totalCount',
			successProperty : 'success'
		}
	}
});

Ext.define('SimpsonGrid', {
	extend : 'Ext.grid.Panel',
	xtype : 'demo-grid',
	store : Ext.create('SimpsonStore'),
	columns : [ {
		text : 'Name',
		dataIndex : 'name'
	}, {
		text : 'Email',
		dataIndex : 'email',
		flex : 1
	}, {
		text : 'Phone',
		dataIndex : 'phone'
	}, {
		text : 'Notes',
		dataIndex : 'notes'
	} ],
	width : 500,
	renderTo : 'gridpanel-demo'
});

Ext.define('AddUserPanel', {
	extend : 'Ext.Panel',	
	layout: {
		type: 'vbox',
		align: 'left',
		pack: 'start'
	},
	width: 500,
	frame: false,
	border: false,
	renderTo : 'add-user-panel',
	items: [{
		xtype: 'textfield',
		fieldLabel: 'Name',
		itemId: 'name'
	},{
		xtype: 'textfield',
		fieldLabel: 'Email',
		itemId: 'email'
	},{
		xtype: 'textfield',
		fieldLabel: 'Phone',
		itemId: 'phone'
	},{
		xtype: 'button',
		text : 'Add User',
		handler : function(btn) {
			var name = btn.up().down('#name').getValue();
			var email = btn.up().down('#email').getValue();
			var phone = btn.up().down('#phone').getValue();
			// Sample JSON object, a "new user".
			var user = {name : name, email : email, phone : phone};
			Ext.Ajax.request({
				url : 'api/users',
				jsonData : user,
				method : 'POST',
				noCache : false,
				success : function(request) {
					alert("Worked! Here's the response JSON:\n\n" + request.responseText);
				},
				failure : function() {
					// Meh
				}
			});
		}
	}]
});

Ext.onReady(function() {
	// JSON GET demo
	var grid = Ext.create('SimpsonGrid');
	grid.getStore().load();
	// JSON POST demo
	Ext.create('AddUserPanel');
});