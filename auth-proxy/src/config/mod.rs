use toml::{from_str, Value};

pub fn get_config() -> Vec<(String, String, bool)> {
    let config: Value =
        from_str(include_str!("../server_config.toml")).expect("Could not load configuration file");

    let authority = config["authority"]
        .as_str()
        .expect("Authority server must be present");

    let servers = config["servers"]
        .as_table()
        .expect("Servers must be present");

    servers
        .iter()
        .map(|(k, t)| {
            let base = t["from"]
                .as_str()
                .expect(&*format!("'from' property must be present for {}", k));
            let proxy = t["to"]
                .as_str()
                .expect(&*format!("'to' property must be present for {}", k));

            (base.to_string(), proxy.to_string(), *k == authority)
        })
        .collect()
}
