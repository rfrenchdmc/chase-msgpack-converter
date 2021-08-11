provider "kubernetes" {
  load_config_file = true
}

resource "kubernetes_namespace" "this" {
  metadata {
    name = "mm2"
  }
}

resource "kubernetes_config_map" this {
  metadata {
    name = "json-mm2-config"
    namespace = kubernetes_namespace.this.metadata.0.name
  }

  data = {
    mm2_properties = "${file("../docker/mm2.properties")}"
  }
}

resource kubernetes_secret docker {
  metadata {
    name="docker-login"
  }
  data = {
    ".dockercfg": file("${path.module}/docker-login.json")
  }
  type = "kubernetes.io/dockercfg"
}

resource "kubernetes_pod" this {
  metadata {
    name = "json-mm2"
    namespace = kubernetes_namespace.this.metadata.0.name
    labels = {
      App = "json-mm2"
    }
  }

  spec {
    container {
      image = "rfrenchdmc/mm2-msgpack"
      name = "json-mm2"
      command = ["/opt/bitnami/kafka/bin/connect-mirror-maker.sh"]
      args = [ "/mm2/mm2.properties" ]
      volume_mount {
        name = "mm2-properties-vol"
        mount_path = "/mm2"
      }
    }
    image_pull_secrets  {
      name = kubernetes_secret.docker.metadata.0.name
    }
    volume {
      name = "mm2-properties-vol"
      config_map {
          name = kubernetes_config_map.this.metadata.0.name
          items {
            key = "mm2_properties"
            path = "mm2.properties"
          }
        }
    }
  }
}


