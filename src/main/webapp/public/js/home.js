Ext.define('SimpsonStore', {
	extend : 'Ext.data.Store',
	fields : [ 'name', 'email', 'phone' ],
	data : {
		'items' : [ {
			'name' : 'Lisa',
			'email' : 'lisa@simpsons.com',
			'phone' : '555-111-1224'
		}, {
			'name' : 'Bart',
			'email' : 'bart@simpsons.com',
			'phone' : '555-222-1234'
		}, {
			'name' : 'Homer',
			'email' : 'home@simpsons.com',
			'phone' : '555-222-1244'
		}, {
			'name' : 'Marge',
			'email' : 'marge@simpsons.com',
			'phone' : '555-222-1254'
		} ]
	},
	proxy : {
		type : 'memory',
		reader : {
			type : 'json',
			root : 'items'
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
	} ],
	height : 200,
	width : 500,
	renderTo : 'gridpanel-demo'
});

Ext.onReady(function() {
	Ext.create('SimpsonGrid');
});