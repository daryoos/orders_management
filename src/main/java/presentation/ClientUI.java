package presentation;

import dataAccess.ClientDAO;
import model.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientUI extends AbstractUI<Client>{
    ClientDAO clientDAO;

    public ClientUI(List<Client> clients, Class<Client> clientClass) {
        super(clients, clientClass);
        clientDAO = new ClientDAO();
    }

    @Override
    public boolean add(Client client) {
        clientDAO.add(client);
        return true;
    }
    @Override
    public void edit(Client client, Client newClient) {
        clientDAO.edit(client, newClient);
    }
    @Override
    public void delete(Client client) {
        clientDAO.delete(client);
    }

}
