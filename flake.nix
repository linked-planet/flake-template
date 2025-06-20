{
  description = "Flake-Templates";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.05";
  };

  outputs = { self }: {
      # Utilized by `nix flake init -t <flake>#<name>`
      templates = {
        atlassian-maven = {
          path = ./atlassian-maven;
          description = "Flake for use with atlassian-plugin projects";
        };
      };
    };
}
