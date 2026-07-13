{{- define "tutormatch.image" -}}
{{- $root := index . 0 -}}
{{- $name := index . 1 -}}
{{- $digest := index . 2 -}}
{{- if $digest -}}
{{- printf "%s/%s@%s" $root.Values.global.imageRegistry $name $digest -}}
{{- else -}}
{{- printf "%s/%s:%s" $root.Values.global.imageRegistry $name (required "global.imageTag must be set when imageDigest is empty" $root.Values.global.imageTag) -}}
{{- end -}}
{{- end -}}
