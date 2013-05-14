Ext.define('LoginForm', {
    extend: 'Ext.form.Panel',
    xtype: 'login-form',
    
    url: 'login',
    standardSubmit: true,
    method: 'POST',
    
    title: 'Login',
    
    frame: true,
    width: 320,
    bodyPadding: 10,
    
    defaultType: 'textfield',
    defaults: {
        anchor: '100%'
    },
    
    listeners: {
        afterRender: function(thisForm, options) {
            this.keyNav = Ext.create('Ext.util.KeyNav', this.el, {                    
                enter: function() {
                	this.getForm().submit();
                },
                scope: this
            });
        }
    },
    
    items: [
        {
            allowBlank: false,
            fieldLabel: 'Username',
            name: 'username',
            emptyText: 'username'
        },
        {
            allowBlank: false,
            fieldLabel: 'Password',
            name: 'password',
            emptyText: 'password',
            inputType: 'password'
        }
    ],
    
    buttons: [
		{
			text: 'Login',
		    listeners: {
		    	click: function(btn) {
		    		btn.up('form').getForm().submit();
		    	}
		    }
		}
	]
    
});

Ext.define('Viewport', {
    extend: 'Ext.container.Viewport',
    alias: 'viewport',

    layout: 'fit',
    
    items: [{
    	xtype: 'panel',
    	layout: {
            type: 'hbox',
            pack: 'center',
            align: 'middle'
        },
        items: [{
	    	xtype: 'login-form'
	    }]
    }]
    
});

Ext.onReady(function() {	
	Ext.create("Viewport");	
});