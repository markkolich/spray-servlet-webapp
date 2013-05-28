Ext.define('SimpsonStore', {
	extend : 'Ext.data.Store',
	fields : [ 'name', 'email', 'phone', 'notes' ],
	proxy: {
        type: 'ajax',
        url: 'api/users',
        reader: {
            type: 'json',
            root: 'results',
            totalProperty: 'totalCount',
            successProperty: 'success'
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
	height : 200,
	width : 500,
	renderTo : 'gridpanel-demo'
});

Ext.onReady(function() {
	var grid = Ext.create('SimpsonGrid');
	grid.getStore().load();
});