package net.Azise.pushjet.Async;

import android.os.AsyncTask;

import net.Azise.pushjet.DatabaseHandler;
import net.Azise.pushjet.PushListAdapter;
import net.Azise.pushjet.PushjetApi.PushjetApi;
import net.Azise.pushjet.PushjetApi.PushjetException;
import net.Azise.pushjet.PushjetApi.PushjetMessage;

import java.util.ArrayList;
import java.util.Arrays;


public class ReceivePushAsync extends AsyncTask<Void, Void, ArrayList<PushjetMessage>> {
    private PushjetApi api;
    private PushListAdapter adapter;
    private PushjetException error;
    private ReceivePushCallback callback;

    public ReceivePushAsync(PushjetApi api, PushListAdapter adapter) {
        this.api = api;
        this.adapter = adapter;
    }

    public void setCallBack(ReceivePushCallback cb) {
        this.callback = cb;
    }

    @Override
    protected ArrayList<PushjetMessage> doInBackground(Void... voids) {
        try {
            return new ArrayList<PushjetMessage>(Arrays.asList(this.api.getNewMessage()));
        } catch (PushjetException e) {
            this.error = e;
            return new ArrayList<PushjetMessage>();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<PushjetMessage> result) {
        DatabaseHandler dbh = new DatabaseHandler(this.api.getContext());
        for (PushjetMessage msg : result)
            dbh.addMessage(msg);
        adapter.addEntries(result);
        if (this.callback != null) {
            this.callback.receivePush(result);
        }
    }
}
